package com.hovr.devicepermissions

data class RuntimeCoordinatorOptions(
    val monitorLocation: Boolean = true,
    val monitorNotifications: Boolean = true,
    val monitorNetwork: Boolean = true,
)
