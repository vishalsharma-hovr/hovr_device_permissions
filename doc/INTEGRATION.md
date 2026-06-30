# Integration

## Android

### settings.gradle

Remove any local `include ':hovr_device_permissions'` project wiring.

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
implementation 'com.github.vishalsharma-hovr:hovr_device_permissions:v1.2.6'
```

For monorepo development, use a local Gradle project instead:

```gradle
// settings.gradle
include ':hovr_device_permissions'
project(':hovr_device_permissions').projectDir =
    new File(settingsDir, '../packages/native/hovr_device_permissions/android/hovr_device_permissions')

// app/build.gradle
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

Remote consumption mirrors Android JitPack: pin a Git tag and let the toolchain fetch sources at build time.

| Host type | Tool | Remote dependency |
|-----------|------|-------------------|
| **Flutter** (rider / driver) | CocoaPods | Git tag in `Podfile` |
| **Native Xcode app** | Swift Package Manager | Git URL + version in `Package.swift` or Xcode |

### Flutter apps — CocoaPods (recommended)

```ruby
pod 'HovrDevicePermissions', :git => 'https://github.com/vishalsharma-hovr/hovr_device_permissions.git', :tag => 'v1.2.6'
```

For monorepo development, use a path dependency instead:

```ruby
pod 'HovrDevicePermissions', :path => '../packages/native/hovr_device_permissions'
```

Then run `pod install` in `ios/`.

### Native Xcode apps — Swift Package Manager

Add the package in Xcode (**File → Add Package Dependencies**) or in your app `Package.swift`:

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
            .product(name: "HovrDevicePermissions", package: "HovrDevicePermissions"),
        ]
    ),
]
```

Local SPM development:

```swift
.package(path: "../packages/native/hovr_device_permissions")
```

**Note:** Flutter iOS projects should stay on CocoaPods. SPM wiring in the Xcode project is not preserved reliably across `flutter build` / project regeneration.

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
