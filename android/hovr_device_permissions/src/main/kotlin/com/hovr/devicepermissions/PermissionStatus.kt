package com.hovr.devicepermissions

enum class PermissionStatus {
    GRANTED,
    DENIED,
    DENIED_PERMANENTLY,
    NOT_DETERMINED,
    RESTRICTED,
    SERVICE_DISABLED,
}

enum class AlertPriority(val level: Int) {
    NETWORK(0),
    LOCATION(1),
    NOTIFICATION(2),
}
