package com.hovr.devicepermissions.location

import com.hovr.devicepermissions.PermissionAccessIssue
import com.hovr.devicepermissions.PermissionStatus

internal object LocationAccessEvaluator {
    fun evaluate(
        locationServicesEnabled: Boolean,
        permissionStatus: PermissionStatus,
    ): PermissionAccessIssue {
        if (!locationServicesEnabled) {
            return PermissionAccessIssue.DEVICE_LOCATION_DISABLED
        }
        return when (permissionStatus) {
            PermissionStatus.GRANTED -> PermissionAccessIssue.GRANTED
            PermissionStatus.NOT_DETERMINED -> PermissionAccessIssue.NOT_DETERMINED
            PermissionStatus.DENIED,
            PermissionStatus.DENIED_PERMANENTLY,
            -> PermissionAccessIssue.APP_LOCATION_DENIED
            else -> PermissionAccessIssue.APP_LOCATION_DENIED
        }
    }
}
