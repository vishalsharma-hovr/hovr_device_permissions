package com.hovr.devicepermissions

internal enum class PermissionAccessIssue {
    GRANTED,
    DEVICE_LOCATION_DISABLED,
    APP_LOCATION_DENIED,
    APP_NOTIFICATION_DENIED,
    NOT_DETERMINED,
}
