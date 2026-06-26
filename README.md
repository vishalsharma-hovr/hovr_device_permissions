# HOVR Device Permissions

Pure-native Android library and iOS CocoaPod for location permission, notification permission, and network connectivity monitoring.

## Features

- Location permission (Activity Result API / `CLLocationManager`)
- Notification permission (`POST_NOTIFICATIONS` API 33+ / `UNUserNotificationCenter`)
- Network connectivity monitoring (`ConnectivityManager` / `NWPathMonitor`)
- Blocking native alerts when permissions are denied or network is offline
- No Flutter plugin — host wires `AppRuntimeCoordinator` in `MainActivity` / `AppDelegate`

## Local development

### Android

```gradle
// android/settings.gradle
include ':hovr_device_permissions'
project(':hovr_device_permissions').projectDir =
    new File(settingsDir, '../native/hovr_device_permissions/android/hovr_device_permissions')

// android/app/build.gradle
implementation project(':hovr_device_permissions')
```

### iOS

```ruby
# ios/Podfile
pod 'HovrDevicePermissions', :path => '../native/hovr_device_permissions/ios'
```

## Git dependency (after publish)

```ruby
pod 'HovrDevicePermissions', :git => 'https://github.com/vishalsharma-hovr/hovr_device_permissions.git', :tag => 'v1.0.0'
```

## Documentation

- [doc/INTEGRATION.md](doc/INTEGRATION.md)
- [doc/PERMISSION_GUIDELINES.md](doc/PERMISSION_GUIDELINES.md)
- [doc/CODING_STANDARDS.md](doc/CODING_STANDARDS.md)
- [doc/ARCHITECTURE.md](doc/ARCHITECTURE.md)
- [doc/NATIVE_API.md](doc/NATIVE_API.md)

## License

Proprietary — HOVR. See [LICENSE](LICENSE).
