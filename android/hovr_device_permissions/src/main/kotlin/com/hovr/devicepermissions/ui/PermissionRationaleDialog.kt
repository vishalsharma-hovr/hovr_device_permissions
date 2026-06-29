package com.hovr.devicepermissions.ui

import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.FragmentActivity

internal class PermissionRationaleDialog(private val activity: FragmentActivity) {
    fun show(
        title: String,
        message: String,
        onContinue: () -> Unit,
        onDecline: () -> Unit,
    ) {
        if (activity.isFinishing || activity.isDestroyed) {
            return
        }
        PermissionAlertDialog.builder(activity)
            .setTitle(title)
            .setMessage(message)
            .setCancelable(false)
            .setPositiveButton("Continue") { _, _ -> onContinue() }
            .setNegativeButton("Not now") { _, _ -> onDecline() }
            .show()
    }
}
