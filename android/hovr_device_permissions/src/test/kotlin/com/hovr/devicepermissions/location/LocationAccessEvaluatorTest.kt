package com.hovr.devicepermissions.location

import com.hovr.devicepermissions.PermissionAccessIssue
import com.hovr.devicepermissions.PermissionStatus
import org.junit.Assert.assertEquals
import org.junit.Test

class LocationAccessEvaluatorTest {
    @Test
    fun deviceLocationDisabled_whenServicesOff() {
        assertEquals(
            PermissionAccessIssue.DEVICE_LOCATION_DISABLED,
            LocationAccessEvaluator.evaluate(
                locationServicesEnabled = false,
                permissionStatus = PermissionStatus.GRANTED,
            ),
        )
    }

    @Test
    fun appLocationDenied_whenPermissionDenied() {
        assertEquals(
            PermissionAccessIssue.APP_LOCATION_DENIED,
            LocationAccessEvaluator.evaluate(
                locationServicesEnabled = true,
                permissionStatus = PermissionStatus.DENIED,
            ),
        )
    }

    @Test
    fun granted_whenServicesOnAndPermissionGranted() {
        assertEquals(
            PermissionAccessIssue.GRANTED,
            LocationAccessEvaluator.evaluate(
                locationServicesEnabled = true,
                permissionStatus = PermissionStatus.GRANTED,
            ),
        )
    }

    @Test
    fun notDetermined_whenNeverRequested() {
        assertEquals(
            PermissionAccessIssue.NOT_DETERMINED,
            LocationAccessEvaluator.evaluate(
                locationServicesEnabled = true,
                permissionStatus = PermissionStatus.NOT_DETERMINED,
            ),
        )
    }
}
