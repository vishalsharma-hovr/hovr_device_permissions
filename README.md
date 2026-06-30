# HOVR Device Permissions

Pure-native Android library and iOS module for location permission, notification permission, and network connectivity monitoring.

## Features

- Location permission (Activity Result API / `CLLocationManager`)
- Notification permission (`POST_NOTIFICATIONS` API 33+ / `UNUserNotificationCenter`)
- Network connectivity monitoring (`ConnectivityManager` / `NWPathMonitor`)
- Blocking native alerts when permissions are denied or network is offline
- No Flutter plugin — host wires `AppRuntimeCoordinator` in `MainActivity` / `AppDelegate`

## Remote dependencies

| Platform | Flutter host | Native host |
|----------|--------------|-------------|
| Android | JitPack `com.github.vishalsharma-hovr:hovr_device_permissions:v1.2.6` | same |
| iOS | CocoaPods Git tag `v1.2.6` | Swift Package Manager `1.2.5` |

See [doc/INTEGRATION.md](doc/INTEGRATION.md) for full wiring.

## Local development

### Android

```gradle
include ':hovr_device_permissions'
project(':hovr_device_permissions').projectDir =
    new File(settingsDir, '../packages/native/hovr_device_permissions/android/hovr_device_permissions')

implementation project(':hovr_device_permissions')
```

### iOS (CocoaPods)

```ruby
pod 'HovrDevicePermissions', :path => '../packages/native/hovr_device_permissions'
```

### iOS (Swift Package Manager)

```swift
.package(path: "../packages/native/hovr_device_permissions")
```

## Documentation

- [doc/INTEGRATION.md](doc/INTEGRATION.md)
- [doc/PERMISSION_GUIDELINES.md](doc/PERMISSION_GUIDELINES.md)
- [doc/CODING_STANDARDS.md](doc/CODING_STANDARDS.md)
- [doc/ARCHITECTURE.md](doc/ARCHITECTURE.md)
- [doc/NATIVE_API.md](doc/NATIVE_API.md)

## License

Proprietary — HOVR. See [LICENSE](LICENSE).
