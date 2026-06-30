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
import com.hovr.devicepermissions.PermissionAccessIssue
import com.hovr.devicepermissions.PermissionActionResolver
import com.hovr.devicepermissions.PermissionAlertReason
import com.hovr.devicepermissions.PermissionCoordinatorAction
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
        alertPresenter.dismissIfPriority(AlertPriority.LOCATION)
    }

    fun ensureAccess() {
        val permissionStatus = currentStatus()
        val issue = LocationAccessEvaluator.evaluate(
            locationServicesEnabled = LocationServiceChecker.isLocationEnabled(activity),
            permissionStatus = permissionStatus,
        )
        handleAccessIssue(issue, permissionStatus)
    }

    override fun onResume(owner: LifecycleOwner) {
        ensureAccess()
    }

    private fun handleAccessIssue(
        issue: PermissionAccessIssue,
        permissionStatus: PermissionStatus,
    ) {
        when (PermissionActionResolver.resolveLocationAction(issue, permissionStatus)) {
            PermissionCoordinatorAction.DISMISS ->
                alertPresenter.dismissIfPriority(AlertPriority.LOCATION)
            PermissionCoordinatorAction.SHOW_DEVICE_LOCATION_DISABLED ->
                showDeviceLocationDisabled()
            PermissionCoordinatorAction.REQUEST_SYSTEM ->
                requestSystemPermission()
            PermissionCoordinatorAction.SHOW_REQUIRED ->
                showAppPermissionRequired()
        }
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
                onContinue = { launchPermissionRequest() },
                onDecline = { showAppPermissionRequired() },
            )
            return
        }
        launchPermissionRequest()
    }

    private fun launchPermissionRequest() {
        pendingSystemRequest = true
        hasRequestedPermission = true
        permissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
    }

    private fun showDeviceLocationDisabled() {
        alertPresenter.showRequired(
            priority = AlertPriority.LOCATION,
            reasonKey = PermissionAlertReason.DEVICE_LOCATION_DISABLED,
            title = "Enable Location Services",
            message = "Location is turned off on this device. Enable it in device location settings.",
            onRetry = { retryAccess() },
            settingsAction = { SettingsIntents.openLocationSourceSettings(activity) },
            onStillRequired = { ensureAccess() },
        )
    }

    private fun showAppPermissionRequired() {
        alertPresenter.showRequired(
            priority = AlertPriority.LOCATION,
            reasonKey = PermissionAlertReason.APP_LOCATION_DENIED,
            title = "Location Permission Required",
            message = "Allow Hovr to access your location in app settings.",
            onRetry = { retryAccess() },
            settingsAction = { SettingsIntents.openAppSettings(activity) },
            onStillRequired = { ensureAccess() },
        )
    }

    private fun retryAccess() {
        val permissionStatus = currentStatus()
        val issue = LocationAccessEvaluator.evaluate(
            locationServicesEnabled = LocationServiceChecker.isLocationEnabled(activity),
            permissionStatus = permissionStatus,
        )
        when (PermissionActionResolver.resolveLocationAction(issue, permissionStatus)) {
            PermissionCoordinatorAction.DISMISS ->
                alertPresenter.dismissIfPriority(AlertPriority.LOCATION)
            PermissionCoordinatorAction.SHOW_DEVICE_LOCATION_DISABLED ->
                ensureAccess()
            PermissionCoordinatorAction.REQUEST_SYSTEM ->
                requestSystemPermission()
            PermissionCoordinatorAction.SHOW_REQUIRED ->
                ensureAccess()
        }
    }
}
