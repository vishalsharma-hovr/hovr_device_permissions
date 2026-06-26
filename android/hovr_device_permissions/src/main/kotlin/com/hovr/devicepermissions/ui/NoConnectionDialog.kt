package com.hovr.devicepermissions.ui

import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.FragmentActivity

internal class NoConnectionDialog(private val activity: FragmentActivity) {
    private var dialog: AlertDialog? = null

    fun show(onRetry: () -> Unit) {
        if (dialog?.isShowing == true || activity.isFinishing || activity.isDestroyed) {
            return
        }
        dialog = AlertDialog.Builder(activity)
            .setTitle("No Internet Connection")
            .setMessage("Please check your internet connection and try again.")
            .setCancelable(false)
            .setPositiveButton("Retry") { _, _ ->
                dismiss()
                onRetry()
            }
            .create()
        dialog?.show()
    }

    fun dismiss() {
        dialog?.dismiss()
        dialog = null
    }
}
