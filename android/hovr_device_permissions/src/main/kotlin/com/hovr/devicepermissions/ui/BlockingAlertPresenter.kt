package com.hovr.devicepermissions.ui

import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.FragmentActivity
import com.hovr.devicepermissions.AlertPriority

internal class BlockingAlertPresenter(private val activity: FragmentActivity) {
    private var activeDialog: AlertDialog? = null
    private var activePriority: AlertPriority? = null

    fun showRequired(
        priority: AlertPriority,
        title: String,
        message: String,
        onRetry: () -> Unit,
        settingsAction: () -> Unit,
    ) {
        if (activeDialog?.isShowing == true && activePriority != null) {
            if (priority.level < activePriority!!.level) {
                return
            }
            dismiss()
        }
        if (activity.isFinishing || activity.isDestroyed) {
            return
        }
        activePriority = priority
        activeDialog = AlertDialog.Builder(activity)
            .setTitle(title)
            .setMessage(message)
            .setCancelable(false)
            .setPositiveButton("Retry") { _, _ ->
                dismiss()
                onRetry()
            }
            .setNegativeButton("Open Settings") { _, _ ->
                dismiss()
                settingsAction()
            }
            .create()
        activeDialog?.show()
    }

    fun dismissIfPriority(priority: AlertPriority) {
        if (activePriority == priority) {
            dismiss()
        }
    }

    fun dismiss() {
        activeDialog?.dismiss()
        activeDialog = null
        activePriority = null
    }
}

internal object SettingsIntents {
    fun openAppSettings(activity: FragmentActivity) {
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
            data = Uri.fromParts("package", activity.packageName, null)
        }
        activity.startActivity(intent)
    }

    fun openNotificationSettings(activity: FragmentActivity) {
        val intent = Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS).apply {
            putExtra(Settings.EXTRA_APP_PACKAGE, activity.packageName)
        }
        activity.startActivity(intent)
    }

    fun openLocationSourceSettings(activity: FragmentActivity) {
        activity.startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
    }
}
