# Architecture

```text
MainActivity / AppDelegate
        │
        ▼
AppRuntimeCoordinator
   ├── NetworkConnectivityCoordinator  (priority 0)
   ├── LocationPermissionCoordinator   (priority 1)
   └── NotificationPermissionCoordinator (priority 2)
```

## Alert priority

Only one blocking permission alert is shown at a time. Priority order:

1. Network offline
2. Location denied / GPS off
3. Notifications denied

## Lifecycle

- **Android:** `attach()` in `onCreate`, `ensureAll()` in `onResume`
- **iOS:** `start()` after root view controller is ready; `ensureAll()` on `willEnterForeground`

## Dart boundary

Flutter does not request permissions or show offline dialogs. Dart may fetch FCM tokens after native grants notification access.
