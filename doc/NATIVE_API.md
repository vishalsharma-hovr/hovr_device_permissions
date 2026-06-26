# Native API

## Android (`com.hovr.devicepermissions`)

```kotlin
class AppRuntimeCoordinator(activity: FragmentActivity) {
    fun attach()
    fun detach()
    fun ensureAll()
}
```

Host: `MainActivity` (must extend `FragmentActivity`).

## iOS (`HovrDevicePermissions`)

```swift
public final class AppRuntimeCoordinator {
    public init(presenter: UIViewController, application: UIApplication)
    public func start()
    public func ensureAll()
    public func stop()
}
```

Host: `AppDelegate` after `FlutterViewController` is the root view controller.
