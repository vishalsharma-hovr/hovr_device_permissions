package com.hovr.devicepermissions.notification

import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import com.hovr.devicepermissions.AlertPriority
import com.hovr.devicepermissions.PermissionStatus
import com.hovr.devicepermissions.ui.BlockingAlertPresenter
import com.hovr.devicepermissions.ui.PermissionRationaleDialog
import com.hovr.devicepermissions.ui.SettingsIntents

internal class NotificationPermissionCoordinator(
    private val activity: FragmentActivity,
    private val alertPresenter: BlockingAlertPresenter,
) : DefaultLifecycleObserver {
    private val rationaleDialog = PermissionRationaleDialog(activity)
    private var attached = false
    private var pendingSystemRequest = false
    private var hasRequestedPermission = false

    private val permissionLauncher = activity.registerForActivityResult(
        ActivityResultContracts.RequestPermission(),
    ) { granted ->
        pendingSystemRequest = false
        if (granted) {
            alertPresenter.dismissIfPriority(AlertPriority.NOTIFICATION)
        } else {
            showDeniedFlow()
        }
    }

    fun attach() {
        if (attached) {
            return
        }
        attached = true
        activity.lifecycle.addObserver(this)
    }

    fun detach() {
        if (!attached) {
            return
        }
        attached = false
        activity.lifecycle.removeObserver(this)
        alertPresenter.dismissIfPriority(AlertPriority.NOTIFICATION)
    }

    fun ensureAccess() {
        val status = NotificationPermissionChecker.currentStatus(activity)
        if (status == PermissionStatus.GRANTED) {
            alertPresenter.dismissIfPriority(AlertPriority.NOTIFICATION)
            return
        }
        if (!NotificationPermissionChecker.requiresRuntimeRequest()) {
            if (status == PermissionStatus.DENIED) {
                showSettingsRequired()
            }
            return
        }
        when (runtimeStatus()) {
            PermissionStatus.GRANTED -> alertPresenter.dismissIfPriority(AlertPriority.NOTIFICATION)
            PermissionStatus.NOT_DETERMINED -> requestRuntimePermission()
            PermissionStatus.DENIED -> showDeniedFlow()
            PermissionStatus.DENIED_PERMANENTLY -> showSettingsRequired()
            else -> showSettingsRequired()
        }
    }

    private fun runtimeStatus(): PermissionStatus {
        val permission = NotificationPermissionChecker.permissionName() ?: return PermissionStatus.DENIED
        val granted = androidx.core.content.ContextCompat.checkSelfPermission(
            activity,
            permission,
        ) == android.content.pm.PackageManager.PERMISSION_GRANTED
        if (granted) {
            return PermissionStatus.GRANTED
        }
        if (ActivityCompat.shouldShowRequestPermissionRationale(activity, permission)) {
            return PermissionStatus.DENIED
        }
        return if (hasRequestedPermission) {
            PermissionStatus.DENIED_PERMANENTLY
        } else {
            PermissionStatus.NOT_DETERMINED
        }
    }

    override fun onResume(owner: LifecycleOwner) {
        ensureAccess()
    }

    private fun requestRuntimePermission() {
        val permission = NotificationPermissionChecker.permissionName() ?: return
        if (pendingSystemRequest) {
            return
        }
        if (ActivityCompat.shouldShowRequestPermissionRationale(activity, permission)) {
            rationaleDialog.show(
                title = "Notifications required",
                message = "Hovr uses notifications for ride updates and important alerts.",
            ) {
                launchPermissionRequest(permission)
            }
            return
        }
        launchPermissionRequest(permission)
    }

    private fun launchPermissionRequest(permission: String) {
        pendingSystemRequest = true
        hasRequestedPermission = true
        permissionLauncher.launch(permission)
    }

    private fun showDeniedFlow() {
        val permission = NotificationPermissionChecker.permissionName()
        if (permission != null &&
            ActivityCompat.shouldShowRequestPermissionRationale(activity, permission)
        ) {
            requestRuntimePermission()
            return
        }
        showSettingsRequired()
    }

    private fun showSettingsRequired() {
        alertPresenter.showRequired(
            priority = AlertPriority.NOTIFICATION,
            title = "Notifications Required",
            message = "Please enable notifications to receive ride updates.",
            onRetry = { ensureAccess() },
            settingsAction = { SettingsIntents.openNotificationSettings(activity) },
        )
    }
}
