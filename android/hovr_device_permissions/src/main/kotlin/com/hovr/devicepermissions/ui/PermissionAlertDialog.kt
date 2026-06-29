package com.hovr.devicepermissions.ui

import androidx.appcompat.app.AlertDialog
import androidx.appcompat.view.ContextThemeWrapper
import androidx.fragment.app.FragmentActivity
import com.hovr.devicepermissions.R

internal object PermissionAlertDialog {
    fun builder(activity: FragmentActivity): AlertDialog.Builder {
        // Flutter hosts use MaterialComponents for the activity theme, not AppCompat.
        // Wrap the activity so AppCompatDialog does not read the host theme directly.
        val themedContext = ContextThemeWrapper(
            activity,
            R.style.HovrPermissionDialogContext,
        )
        return AlertDialog.Builder(themedContext, R.style.HovrPermissionAlertDialog)
    }
}
