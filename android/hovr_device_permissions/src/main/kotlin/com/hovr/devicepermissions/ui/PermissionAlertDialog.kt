package com.hovr.devicepermissions.ui

import androidx.appcompat.app.AlertDialog
import androidx.appcompat.view.ContextThemeWrapper
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import com.hovr.devicepermissions.R

internal object PermissionAlertDialog {
    fun builder(activity: FragmentActivity): AlertDialog.Builder {
        // Must wrap the activity, not applicationContext — dialogs need a valid window token.
        val themedContext = ContextThemeWrapper(
            activity,
            R.style.HovrPermissionDialogContext,
        )
        return AlertDialog.Builder(themedContext, R.style.HovrPermissionAlertDialog)
    }

    fun showWithStyledButtons(activity: FragmentActivity, dialog: AlertDialog) {
        activity.runWhenWindowReady {
            dialog.show()
            applyActionButtonColors(dialog)
        }
    }

    private fun applyActionButtonColors(dialog: AlertDialog) {
        val color = ContextCompat.getColor(
            dialog.context,
            R.color.hovr_permission_dialog_button_text,
        )
        listOf(
            AlertDialog.BUTTON_POSITIVE,
            AlertDialog.BUTTON_NEGATIVE,
            AlertDialog.BUTTON_NEUTRAL,
        ).forEach { which ->
            dialog.getButton(which)?.setTextColor(color)
        }
    }
}
