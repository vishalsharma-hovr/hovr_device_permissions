package com.hovr.devicepermissions.notification

import android.content.Context
import com.hovr.devicepermissions.PermissionStatus
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [33])
class NotificationPermissionCheckerTest {
    @Test
    fun requiresRuntimeRequest_onApi33() {
        assertEquals(true, NotificationPermissionChecker.requiresRuntimeRequest())
    }

    @Test
    fun permissionName_onApi33_isPostNotifications() {
        assertEquals(
            "android.permission.POST_NOTIFICATIONS",
            NotificationPermissionChecker.permissionName(),
        )
    }
}
