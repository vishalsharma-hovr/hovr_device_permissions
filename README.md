# HOVR Device Permissions

Pure-native Android library and iOS module for location permission, notification permission, and network connectivity monitoring.

## Features

- Location permission (Activity Result API / `CLLocationManager`)
- Notification permission (`POST_NOTIFICATIONS` API 33+ / `UNUserNotificationCenter`)
- Network connectivity monitoring (`ConnectivityManager` / `NWPathMonitor`)
- Blocking native alerts when permissions are denied or network is offline
- No Flutter plugin — host wires `AppRuntimeCoordinator` in `MainActivity` / `AppDelegate`

## Host apps (rider / driver) — remote only

Committed app config uses **GitHub + JitPack / CocoaPods**, not local paths.

| Platform | Remote coordinate |
|----------|-------------------|
| Android | `com.github.vishalsharma-hovr:hovr_device_permissions:v1.2.4` |
| iOS | CocoaPods Git tag `v1.2.6` or SPM exact `1.2.6` |

See [doc/INTEGRATION.md](doc/INTEGRATION.md).

## Developing this module

1. Edit source under `packages/native/hovr_device_permissions/` (rider) or clone for driver.
2. **Temporarily** wire local path in the host app — [doc/LOCAL_DEVELOPMENT.md](doc/LOCAL_DEVELOPMENT.md).
3. Run unit tests and manual QA on device.
4. Publish: `./tool/publish_to_github.sh v1.x.x`
5. Trigger JitPack for the new Android tag.
6. **Revert** host apps to remote coordinates and bump the version tag.

## Documentation

- [doc/LOCAL_DEVELOPMENT.md](doc/LOCAL_DEVELOPMENT.md) — test locally, then publish
- [doc/INTEGRATION.md](doc/INTEGRATION.md) — production wiring
- [doc/PERMISSION_GUIDELINES.md](doc/PERMISSION_GUIDELINES.md)
- [doc/CODING_STANDARDS.md](doc/CODING_STANDARDS.md)
- [doc/ARCHITECTURE.md](doc/ARCHITECTURE.md)
- [doc/NATIVE_API.md](doc/NATIVE_API.md)

## License

Proprietary — HOVR. See [LICENSE](LICENSE).
