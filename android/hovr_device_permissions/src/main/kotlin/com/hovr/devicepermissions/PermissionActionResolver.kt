package com.hovr.devicepermissions

internal object PermissionActionResolver {
    fun resolveLocationAction(
        issue: PermissionAccessIssue,
        permissionStatus: PermissionStatus,
    ): PermissionCoordinatorAction {
        return when (issue) {
            PermissionAccessIssue.GRANTED -> PermissionCoordinatorAction.DISMISS
            PermissionAccessIssue.DEVICE_LOCATION_DISABLED ->
                PermissionCoordinatorAction.SHOW_DEVICE_LOCATION_DISABLED
            PermissionAccessIssue.NOT_DETERMINED -> PermissionCoordinatorAction.REQUEST_SYSTEM
            PermissionAccessIssue.APP_LOCATION_DENIED -> when (permissionStatus) {
                PermissionStatus.NOT_DETERMINED,
                PermissionStatus.DENIED,
                -> PermissionCoordinatorAction.REQUEST_SYSTEM
                else -> PermissionCoordinatorAction.SHOW_REQUIRED
            }
            PermissionAccessIssue.APP_NOTIFICATION_DENIED -> PermissionCoordinatorAction.DISMISS
        }
    }

    fun resolveRuntimePermissionAction(status: PermissionStatus): PermissionCoordinatorAction {
        return when (status) {
            PermissionStatus.GRANTED -> PermissionCoordinatorAction.DISMISS
            PermissionStatus.NOT_DETERMINED,
            PermissionStatus.DENIED,
            -> PermissionCoordinatorAction.REQUEST_SYSTEM
            PermissionStatus.DENIED_PERMANENTLY -> PermissionCoordinatorAction.SHOW_REQUIRED
            else -> PermissionCoordinatorAction.SHOW_REQUIRED
        }
    }
}
