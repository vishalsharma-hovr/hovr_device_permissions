package com.hovr.devicepermissions

import android.os.Handler
import android.os.Looper
import androidx.fragment.app.FragmentActivity
import com.hovr.devicepermissions.location.LocationPermissionCoordinator
import com.hovr.devicepermissions.network.NetworkConnectivityCoordinator
import com.hovr.devicepermissions.notification.NotificationPermissionCoordinator
import com.hovr.devicepermissions.ui.BlockingAlertPresenter

class AppRuntimeCoordinator(private val activity: FragmentActivity) {
    private val mainHandler = Handler(Looper.getMainLooper())
    private val alertPresenter = BlockingAlertPresenter(activity)
    private val networkCoordinator = NetworkConnectivityCoordinator(activity)
    private val locationCoordinator = LocationPermissionCoordinator(activity, alertPresenter)
    private val notificationCoordinator =
        NotificationPermissionCoordinator(activity, alertPresenter)
    private var attached = false

    fun attach() {
        if (attached) {
            return
        }
        attached = true
        networkCoordinator.attach()
        locationCoordinator.attach()
        notificationCoordinator.attach()
    }

    fun detach() {
        if (!attached) {
            return
        }
        attached = false
        mainHandler.removeCallbacksAndMessages(null)
        notificationCoordinator.detach()
        locationCoordinator.detach()
        networkCoordinator.detach()
        alertPresenter.dismiss()
    }

    fun ensureAll() {
        networkCoordinator.recheck()
        locationCoordinator.ensureAccess()
        mainHandler.postDelayed(
            { notificationCoordinator.ensureAccess() },
            PermissionLimits.NOTIFICATION_PROMPT_DELAY_MS,
        )
    }
}
