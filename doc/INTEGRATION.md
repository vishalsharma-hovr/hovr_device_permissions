# Integration

## Android

### settings.gradle

```gradle
include ':hovr_device_permissions'
project(':hovr_device_permissions').projectDir =
    new File(settingsDir, '../native/hovr_device_permissions/android/hovr_device_permissions')
```

### app/build.gradle

```gradle
implementation project(':hovr_device_permissions')
```

### MainActivity.kt

```kotlin
import com.hovr.devicepermissions.AppRuntimeCoordinator

private lateinit var runtimeCoordinator: AppRuntimeCoordinator

override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    // ... existing setup ...
    runtimeCoordinator = AppRuntimeCoordinator(this)
    runtimeCoordinator.attach()
}

override fun onResume() {
    super.onResume()
    runtimeCoordinator.ensureAll()
}

override fun onDestroy() {
    runtimeCoordinator.detach()
    super.onDestroy()
}
```

## iOS

### Podfile

```ruby
pod 'HovrDevicePermissions', :git => 'https://github.com/vishalsharma-hovr/hovr_device_permissions.git', :tag => 'v1.2.0'
```

For monorepo development, use a path dependency instead:

```ruby
pod 'HovrDevicePermissions', :path => '../native/hovr_device_permissions/ios'
```

### AppDelegateBootstrap.swift

```swift
import HovrDevicePermissions

// After FlutterViewController is root:
appDelegate.runtimeCoordinator = AppRuntimeCoordinator(
    presenter: controller,
    application: application
)
appDelegate.runtimeCoordinator?.start()
appDelegate.runtimeCoordinator?.ensureAll()
```

### AppDelegate.swift

```swift
var runtimeCoordinator: AppRuntimeCoordinator?

func applicationWillEnterForeground(_ application: UIApplication) {
    runtimeCoordinator?.ensureAll()
}
```

### Driver app (network + notifications only)

When the host owns background location (e.g. Hovr Driver), disable the module location coordinator:

```kotlin
// Android MainActivity.kt
runtimeCoordinator = AppRuntimeCoordinator(
    this,
    RuntimeCoordinatorOptions(monitorLocation = false),
)
```

```swift
// iOS AppDelegate.swift
runtimeCoordinator = AppRuntimeCoordinator(
    presenter: controller,
    application: application,
    options: RuntimeCoordinatorOptions(monitorLocation: false)
)
```

## Remove from host

- `LocationServiceChannelRegistrar`, `LocationCheckerPlugin`, `notificationServices` channel
- Host `NetworkMonitor` / `NoLocationBanner`
- Dart permission and splash connectivity checks
