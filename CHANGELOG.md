## 1.1.0

- Device vs app permission classification via `LocationAccessEvaluator` and `NotificationAccessEvaluator`.
- iOS typed `SettingsDestination` routing (app settings vs instructional device-location flow).
- Blocking alerts stay visible until permission is granted; re-show on dismiss.
- `PermissionStatusMapper` access-issue mapping and unit/XCTest coverage.
- Updated `PERMISSION_GUIDELINES.md` with iOS routing table and manual QA matrix.

## 1.0.0

- Initial release: `AppRuntimeCoordinator` with location, notification, and network coordinators for Android and iOS.
