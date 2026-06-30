# Integration

Host apps (rider, driver) **must use remote dependencies by default**. Local path wiring is for module development only — see [LOCAL_DEVELOPMENT.md](LOCAL_DEVELOPMENT.md).

## Current release

| Platform | Coordinate |
|----------|------------|
| Android (JitPack) | `com.github.vishalsharma-hovr:hovr_device_permissions:v1.2.4` |
| iOS (CocoaPods) | `pod 'HovrDevicePermissions', :git => '…', :tag => 'v1.2.6'` |
| iOS (SPM) | `.package(url: "…/hovr_device_permissions.git", exact: "1.2.6")` |

## Android

### settings.gradle

No local module include on committed branches:

```gradle
include ":app"
```

### build.gradle (root)

```gradle
allprojects {
    repositories {
        google()
        mavenCentral()
        maven { url 'https://jitpack.io' }
    }
}
```

### app/build.gradle

```gradle
implementation 'com.github.vishalsharma-hovr:hovr_device_permissions:v1.2.4'
```

New tags must be built on JitPack before Gradle can resolve them:  
https://jitpack.io/#vishalsharma-hovr/hovr_device_permissions

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

| Host type | Tool | Remote dependency |
|-----------|------|-------------------|
| **Flutter** (rider / driver) | CocoaPods | Git tag in `Podfile` |
| **Native Xcode app** | Swift Package Manager | Git URL + version |

### Flutter apps — CocoaPods

```ruby
pod 'HovrDevicePermissions', :git => 'https://github.com/vishalsharma-hovr/hovr_device_permissions.git', :tag => 'v1.2.6'
```

### Native Xcode apps — Swift Package Manager

```swift
dependencies: [
    .package(
        url: "https://github.com/vishalsharma-hovr/hovr_device_permissions.git",
        exact: "1.2.6"
    ),
],
targets: [
    .target(
        name: "YourApp",
        dependencies: [
            .product(name: "HovrDevicePermissions", package: "hovr_device_permissions"),
        ]
    ),
]
```

**Note:** Flutter iOS projects should stay on CocoaPods.

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

```kotlin
runtimeCoordinator = AppRuntimeCoordinator(
    this,
    RuntimeCoordinatorOptions(monitorLocation = false),
)
```

```swift
runtimeCoordinator = AppRuntimeCoordinator(
    presenter: controller,
    application: application,
    options: RuntimeCoordinatorOptions(monitorLocation: false)
)
```

## Local testing

See [LOCAL_DEVELOPMENT.md](LOCAL_DEVELOPMENT.md). **Do not commit** local `project()` or `:path` pod wiring to rider/driver `main`.

## Remove from host

- `LocationServiceChannelRegistrar`, `LocationCheckerPlugin`, `notificationServices` channel
- Host `NetworkMonitor` / `NoLocationBanner`
- Dart permission and splash connectivity checks
