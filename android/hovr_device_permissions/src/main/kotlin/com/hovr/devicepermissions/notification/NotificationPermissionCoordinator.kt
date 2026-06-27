package com.hovr.devicepermissions.notification

import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import com.hovr.devicepermissions.AlertPriority
import com.hovr.devicepermissions.PermissionAlertReason
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
            ensureAccess()
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
                showAppPermissionRequired()
            }
            return
        }
        when (runtimeStatus()) {
            PermissionStatus.GRANTED -> alertPresenter.dismissIfPriority(AlertPriority.NOTIFICATION)
            PermissionStatus.NOT_DETERMINED,
            PermissionStatus.DENIED,
            PermissionStatus.DENIED_PERMANENTLY,
            -> showAppPermissionRequired()
            else -> showAppPermissionRequired()
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
                onContinue = { launchPermissionRequest(permission) },
                onDecline = { showAppPermissionRequired() },
            )
            return
        }
        launchPermissionRequest(permission)
    }

    private fun launchPermissionRequest(permission: String) {
        pendingSystemRequest = true
        hasRequestedPermission = true
        permissionLauncher.launch(permission)
    }

    private fun showAppPermissionRequired() {
        alertPresenter.showRequired(
            priority = AlertPriority.NOTIFICATION,
            reasonKey = PermissionAlertReason.APP_NOTIFICATION_DENIED,
            title = "Notifications Permission Required",
            message = "Allow Hovr to send notifications in app settings.",
            onRetry = { retryAccess() },
            settingsAction = { SettingsIntents.openNotificationSettings(activity) },
            onStillRequired = { ensureAccess() },
        )
    }

    private fun retryAccess() {
        val status = NotificationPermissionChecker.currentStatus(activity)
        if (status == PermissionStatus.GRANTED) {
            alertPresenter.dismissIfPriority(AlertPriority.NOTIFICATION)
            return
        }
        if (!NotificationPermissionChecker.requiresRuntimeRequest()) {
            ensureAccess()
            return
        }
        when (runtimeStatus()) {
            PermissionStatus.GRANTED -> alertPresenter.dismissIfPriority(AlertPriority.NOTIFICATION)
            PermissionStatus.NOT_DETERMINED,
            PermissionStatus.DENIED,
            -> requestRuntimePermission()
            PermissionStatus.DENIED_PERMANENTLY -> ensureAccess()
            else -> ensureAccess()
        }
    }
}
