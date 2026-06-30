package com.hovr.devicepermissions

import org.junit.Assert.assertEquals
import org.junit.Test

class PermissionActionResolverTest {
    @Test
    fun locationGranted_dismisses() {
        assertEquals(
            PermissionCoordinatorAction.DISMISS,
            PermissionActionResolver.resolveLocationAction(
                issue = PermissionAccessIssue.GRANTED,
                permissionStatus = PermissionStatus.GRANTED,
            ),
        )
    }

    @Test
    fun locationNotDetermined_requestsSystem() {
        assertEquals(
            PermissionCoordinatorAction.REQUEST_SYSTEM,
            PermissionActionResolver.resolveLocationAction(
                issue = PermissionAccessIssue.NOT_DETERMINED,
                permissionStatus = PermissionStatus.NOT_DETERMINED,
            ),
        )
    }

    @Test
    fun locationDenied_requestsSystem() {
        assertEquals(
            PermissionCoordinatorAction.REQUEST_SYSTEM,
            PermissionActionResolver.resolveLocationAction(
                issue = PermissionAccessIssue.APP_LOCATION_DENIED,
                permissionStatus = PermissionStatus.DENIED,
            ),
        )
    }

    @Test
    fun locationDeniedPermanently_showsRequired() {
        assertEquals(
            PermissionCoordinatorAction.SHOW_REQUIRED,
            PermissionActionResolver.resolveLocationAction(
                issue = PermissionAccessIssue.APP_LOCATION_DENIED,
                permissionStatus = PermissionStatus.DENIED_PERMANENTLY,
            ),
        )
    }

    @Test
    fun locationDeviceDisabled_showsDeviceDialog() {
        assertEquals(
            PermissionCoordinatorAction.SHOW_DEVICE_LOCATION_DISABLED,
            PermissionActionResolver.resolveLocationAction(
                issue = PermissionAccessIssue.DEVICE_LOCATION_DISABLED,
                permissionStatus = PermissionStatus.GRANTED,
            ),
        )
    }

    @Test
    fun runtimeNotDetermined_requestsSystem() {
        assertEquals(
            PermissionCoordinatorAction.REQUEST_SYSTEM,
            PermissionActionResolver.resolveRuntimePermissionAction(
                PermissionStatus.NOT_DETERMINED,
            ),
        )
    }

    @Test
    fun runtimeDenied_requestsSystem() {
        assertEquals(
            PermissionCoordinatorAction.REQUEST_SYSTEM,
            PermissionActionResolver.resolveRuntimePermissionAction(
                PermissionStatus.DENIED,
            ),
        )
    }

    @Test
    fun runtimeDeniedPermanently_showsRequired() {
        assertEquals(
            PermissionCoordinatorAction.SHOW_REQUIRED,
            PermissionActionResolver.resolveRuntimePermissionAction(
                PermissionStatus.DENIED_PERMANENTLY,
            ),
        )
    }

    @Test
    fun runtimeGranted_dismisses() {
        assertEquals(
            PermissionCoordinatorAction.DISMISS,
            PermissionActionResolver.resolveRuntimePermissionAction(
                PermissionStatus.GRANTED,
            ),
        )
    }
}
