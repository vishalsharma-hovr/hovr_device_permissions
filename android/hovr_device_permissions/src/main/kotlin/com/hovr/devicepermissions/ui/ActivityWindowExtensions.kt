package com.hovr.devicepermissions.ui

import androidx.fragment.app.FragmentActivity

internal fun FragmentActivity.runWhenWindowReady(action: () -> Unit) {
    if (isFinishing || isDestroyed) {
        return
    }
    val decorView = window?.decorView ?: return
    decorView.post {
        if (!isFinishing && !isDestroyed) {
            action()
        }
    }
}
