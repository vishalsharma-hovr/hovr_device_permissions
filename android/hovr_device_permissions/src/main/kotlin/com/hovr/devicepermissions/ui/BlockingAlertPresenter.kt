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
    private var activeReasonKey: String? = null
    private var intentionalDismiss = false
    private var onUnexpectedDismiss: (() -> Unit)? = null

    fun showRequired(
        priority: AlertPriority,
        reasonKey: String,
        title: String,
        message: String,
        onRetry: () -> Unit,
        settingsAction: () -> Unit,
        onStillRequired: () -> Unit,
    ) {
        onUnexpectedDismiss = onStillRequired
        if (activeDialog?.isShowing == true &&
            activePriority == priority &&
            activeReasonKey == reasonKey
        ) {
            return
        }
        if (activeDialog?.isShowing == true && activePriority != null) {
            if (priority.level < activePriority!!.level) {
                return
            }
            dismissInternal(intentional = true)
        }
        if (activity.isFinishing || activity.isDestroyed) {
            return
        }
        activePriority = priority
        activeReasonKey = reasonKey
        intentionalDismiss = false
        activeDialog = AlertDialog.Builder(activity)
            .setTitle(title)
            .setMessage(message)
            .setCancelable(false)
            .setPositiveButton("Retry") { _, _ -> onRetry() }
            .setNegativeButton("Open Settings") { _, _ -> settingsAction() }
            .create()
            .also { dialog ->
                dialog.setCanceledOnTouchOutside(false)
                dialog.setOnDismissListener {
                    if (intentionalDismiss) {
                        return@setOnDismissListener
                    }
                    if (activeDialog !== dialog) {
                        return@setOnDismissListener
                    }
                    activeDialog = null
                    onStillRequired()
                }
            }
        activeDialog?.show()
    }

    fun dismissIfPriority(priority: AlertPriority) {
        if (activePriority == priority) {
            dismissInternal(intentional = true)
        }
    }

    fun dismiss() {
        dismissInternal(intentional = true)
    }

    private fun dismissInternal(intentional: Boolean) {
        intentionalDismiss = intentional
        activeDialog?.setOnDismissListener(null)
        activeDialog?.dismiss()
        activeDialog = null
        activePriority = null
        activeReasonKey = null
        onUnexpectedDismiss = null
        intentionalDismiss = false
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
