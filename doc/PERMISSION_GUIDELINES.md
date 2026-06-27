# Permission guidelines

Maps implementation to official platform documentation.

## Android

| Step | Official guide | Implementation |
|------|----------------|----------------|
| Check permission | [Request runtime permissions](https://developer.android.com/training/permissions/requesting) | `ContextCompat.checkSelfPermission` in coordinators |
| Request permission | Activity Result API | `registerForActivityResult(RequestPermission)` |
| Rationale | Educational UI before re-request | `PermissionRationaleDialog` |
| Device location off | [Location settings](https://developer.android.com/training/permissions/requesting) | `LocationAccessEvaluator` → `SettingsIntents.openLocationSourceSettings` |
| App location denied | Open app settings | `LocationAccessEvaluator` → `SettingsIntents.openAppSettings` |
| App notifications denied | [Notification permission](https://developer.android.com/develop/ui/views/notifications/notification-permission) | `SettingsIntents.openNotificationSettings` |
| Network | [Monitor connectivity](https://developer.android.com/training/monitoring-device-state/connectivity-status-type) | `registerDefaultNetworkCallback` |

## iOS

| Step | Official guide | Implementation |
|------|----------------|----------------|
| Location strings | [Core Location authorization](https://developer.apple.com/documentation/corelocation/requesting-authorization-to-use-location-services) | Host `Info.plist` keys |
| Classify issue | `LocationAccessEvaluator` / `NotificationAccessEvaluator` | `PermissionAccessIssue` |
| Device location off | No public deep link | Instructional alert + **Got it** + Retry; user navigates manually to Privacy & Security → Location Services |
| Device restricted | Screen Time / MDM | Instructional alert + **Got it** + Retry |
| App location denied | `UIApplication.openSettingsURLString` | **Open App Settings** → HOVR → Location |
| App notifications denied (iOS 16+) | `UIApplication.openNotificationSettingsURLString` | **Open Notification Settings** |
| Location request | `requestWhenInUseAuthorization` when `.notDetermined` | Retry on blocking alert |
| APNs | After authorized | `registerForRemoteNotifications()` |
| Network | [NWPathMonitor](https://developer.apple.com/documentation/network/nwpathmonitor) | `NetworkConnectivityCoordinator` |

Apple does **not** provide an App Store-safe URL for device-wide Location Services. Do not use `prefs:` or `App-Prefs:` schemes.

## Manual QA matrix

| Scenario | Platform | Expected popup | Settings button |
|----------|----------|----------------|-----------------|
| Device GPS off | Android | Enable Location Services | Opens device location settings |
| Device GPS off | iOS | Enable Location Services (with steps) | **Got it** (no navigation) |
| App location denied | Both | Location Permission Required | Opens app location settings |
| App notifications denied | Both | Notifications Permission Required | Opens app notification settings |
| Restricted (Screen Time) | iOS | Location Restricted | **Got it** (no navigation) |

## Host prerequisites

### AndroidManifest.xml (host)

- `ACCESS_FINE_LOCATION`
- `ACCESS_COARSE_LOCATION`
- `POST_NOTIFICATIONS`

### Info.plist (host)

- `NSLocationWhenInUseUsageDescription`
- `NSUserNotificationsUsageDescription`
