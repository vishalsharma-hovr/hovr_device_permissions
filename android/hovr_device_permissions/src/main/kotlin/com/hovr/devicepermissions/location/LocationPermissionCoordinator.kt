package com.hovr.devicepermissions.location

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import com.hovr.devicepermissions.AlertPriority
import com.hovr.devicepermissions.PermissionStatus
import com.hovr.devicepermissions.ui.BlockingAlertPresenter
import com.hovr.devicepermissions.ui.PermissionRationaleDialog
import com.hovr.devicepermissions.ui.SettingsIntents

internal class LocationPermissionCoordinator(
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
            alertPresenter.dismissIfPriority(AlertPriority.LOCATION)
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
        alertPresenter.dismissIfPriority(AlertPriority.LOCATION)
    }

    fun ensureAccess() {
        if (!LocationServiceChecker.isLocationEnabled(activity)) {
            showGpsDisabled()
            return
        }
        when (currentStatus()) {
            PermissionStatus.GRANTED -> alertPresenter.dismissIfPriority(AlertPriority.LOCATION)
            PermissionStatus.NOT_DETERMINED -> requestSystemPermission()
            PermissionStatus.DENIED -> showDeniedFlow()
            PermissionStatus.DENIED_PERMANENTLY -> showSettingsRequired()
            else -> showSettingsRequired()
        }
    }

    override fun onResume(owner: LifecycleOwner) {
        ensureAccess()
    }

    private fun currentStatus(): PermissionStatus {
        val granted = ContextCompat.checkSelfPermission(
            activity,
            Manifest.permission.ACCESS_FINE_LOCATION,
        ) == PackageManager.PERMISSION_GRANTED
        if (granted) {
            return PermissionStatus.GRANTED
        }
        val shouldExplain = ActivityCompat.shouldShowRequestPermissionRationale(
            activity,
            Manifest.permission.ACCESS_FINE_LOCATION,
        )
        if (shouldExplain) {
            return PermissionStatus.DENIED
        }
        return if (hasRequestedPermission) {
            PermissionStatus.DENIED_PERMANENTLY
        } else {
            PermissionStatus.NOT_DETERMINED
        }
    }

    private fun requestSystemPermission() {
        if (pendingSystemRequest) {
            return
        }
        if (ActivityCompat.shouldShowRequestPermissionRationale(
                activity,
                Manifest.permission.ACCESS_FINE_LOCATION,
            )
        ) {
            rationaleDialog.show(
                title = "Location required",
                message = "Hovr needs your location to find nearby rides and improve pickups.",
            ) {
                launchPermissionRequest()
            }
            return
        }
        launchPermissionRequest()
    }

    private fun launchPermissionRequest() {
        pendingSystemRequest = true
        hasRequestedPermission = true
        permissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
    }

    private fun showDeniedFlow() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(
                activity,
                Manifest.permission.ACCESS_FINE_LOCATION,
            )
        ) {
            requestSystemPermission()
            return
        }
        showSettingsRequired()
    }

    private fun showGpsDisabled() {
        alertPresenter.showRequired(
            priority = AlertPriority.LOCATION,
            title = "Enable Location Services",
            message = "Location services are off. Turn them on to find nearby rides.",
            onRetry = { ensureAccess() },
            settingsAction = { SettingsIntents.openLocationSourceSettings(activity) },
        )
    }

    private fun showSettingsRequired() {
        alertPresenter.showRequired(
            priority = AlertPriority.LOCATION,
            title = "Location Required",
            message = "Please enable location access to find nearby rides.",
            onRetry = { ensureAccess() },
            settingsAction = { SettingsIntents.openAppSettings(activity) },
        )
    }
}
