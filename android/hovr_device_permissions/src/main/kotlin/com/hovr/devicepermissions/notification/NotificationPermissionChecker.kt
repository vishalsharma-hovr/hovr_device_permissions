package com.hovr.devicepermissions.notification

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.hovr.devicepermissions.PermissionStatus

internal object NotificationPermissionChecker {
    fun currentStatus(context: Context): PermissionStatus {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val granted = ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS,
            ) == PackageManager.PERMISSION_GRANTED
            return if (granted) PermissionStatus.GRANTED else PermissionStatus.DENIED
        }
        val enabled = NotificationManagerCompat.from(context).areNotificationsEnabled()
        return if (enabled) PermissionStatus.GRANTED else PermissionStatus.DENIED
    }

    fun requiresRuntimeRequest(): Boolean {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU
    }

    fun permissionName(): String? {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            Manifest.permission.POST_NOTIFICATIONS
        } else {
            null
        }
    }
}
