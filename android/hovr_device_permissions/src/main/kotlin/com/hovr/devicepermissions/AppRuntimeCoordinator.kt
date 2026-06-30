package com.hovr.devicepermissions

import android.os.Handler
import android.os.Looper
import androidx.fragment.app.FragmentActivity
import com.hovr.devicepermissions.location.LocationPermissionCoordinator
import com.hovr.devicepermissions.network.NetworkConnectivityCoordinator
import com.hovr.devicepermissions.notification.NotificationPermissionCoordinator
import com.hovr.devicepermissions.ui.BlockingAlertPresenter

class AppRuntimeCoordinator(
    private val activity: FragmentActivity,
    private val options: RuntimeCoordinatorOptions = RuntimeCoordinatorOptions(),
) {
    private val mainHandler = Handler(Looper.getMainLooper())
    private val alertPresenter = BlockingAlertPresenter(activity)
    private val networkCoordinator =
        if (options.monitorNetwork) NetworkConnectivityCoordinator(activity) else null
    private val locationCoordinator =
        if (options.monitorLocation) {
            LocationPermissionCoordinator(activity, alertPresenter)
        } else {
            null
        }
    private val notificationCoordinator =
        if (options.monitorNotifications) {
            NotificationPermissionCoordinator(activity, alertPresenter)
        } else {
            null
        }
    private var attached = false

    fun attach() {
        if (attached) {
            return
        }
        attached = true
        networkCoordinator?.attach()
        locationCoordinator?.attach()
        notificationCoordinator?.attach()
    }

    fun detach() {
        if (!attached) {
            return
        }
        attached = false
        mainHandler.removeCallbacksAndMessages(null)
        notificationCoordinator?.detach()
        locationCoordinator?.detach()
        networkCoordinator?.detach()
        alertPresenter.dismiss()
    }

    fun ensureAll() {
        networkCoordinator?.recheck()
        locationCoordinator?.ensureAccess()
        if (notificationCoordinator != null) {
            mainHandler.postDelayed(
                { notificationCoordinator.ensureAccess() },
                PermissionLimits.NOTIFICATION_PROMPT_DELAY_MS,
            )
        }
    }
}
