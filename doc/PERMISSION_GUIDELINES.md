# Permission guidelines

Maps implementation to official platform documentation.

## Android

| Step | Official guide | Implementation |
|------|----------------|----------------|
| Check permission | [Request runtime permissions](https://developer.android.com/training/permissions/requesting) | `ContextCompat.checkSelfPermission` in coordinators |
| Request permission | Activity Result API | `registerForActivityResult(RequestPermission)` |
| Rationale | Educational UI before re-request | `PermissionRationaleDialog` |
| Permanent deny | Open Settings | `SettingsIntents.openAppSettings` / `openNotificationSettings` |
| Notifications API 33+ | [Notification permission](https://developer.android.com/develop/ui/views/notifications/notification-permission) | `NotificationPermissionChecker` |
| Network | [Monitor connectivity](https://developer.android.com/training/monitoring-device-state/connectivity-status-type) | `registerDefaultNetworkCallback` |

## iOS

| Step | Official guide | Implementation |
|------|----------------|----------------|
| Location strings | [Core Location authorization](https://developer.apple.com/documentation/corelocation/requesting-authorization-to-use-location-services) | Host `Info.plist` keys |
| Location request | `requestWhenInUseAuthorization` when `.notDetermined` | `LocationPermissionCoordinator` |
| Location denied | HIG — Settings, no re-request | `BlockingAlertPresenter` |
| Notifications | [Asking permission](https://developer.apple.com/documentation/usernotifications/asking-permission-to-use-notifications) | `getNotificationSettings` then `requestAuthorization` |
| APNs | After authorized | `registerForRemoteNotifications()` |
| Network | [NWPathMonitor](https://developer.apple.com/documentation/network/nwpathmonitor) | `NetworkConnectivityCoordinator` |

## Host prerequisites

### AndroidManifest.xml (host)

- `ACCESS_FINE_LOCATION`
- `ACCESS_COARSE_LOCATION`
- `POST_NOTIFICATIONS`

### Info.plist (host)

- `NSLocationWhenInUseUsageDescription`
- `NSUserNotificationsUsageDescription`
