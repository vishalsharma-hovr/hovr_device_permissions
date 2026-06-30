# Native API

## Android (`com.hovr.devicepermissions`)

```kotlin
data class RuntimeCoordinatorOptions(
    val monitorLocation: Boolean = true,
    val monitorNotifications: Boolean = true,
    val monitorNetwork: Boolean = true,
)

class AppRuntimeCoordinator(
    activity: FragmentActivity,
    options: RuntimeCoordinatorOptions = RuntimeCoordinatorOptions(),
) {
    fun attach()
    fun detach()
    fun ensureAll()
}
```

Host: `MainActivity` (must extend `FragmentActivity`).

## iOS (`HovrDevicePermissions`)

```swift
public struct RuntimeCoordinatorOptions {
    public init(
        monitorLocation: Bool = true,
        monitorNotifications: Bool = true,
        monitorNetwork: Bool = true
    )
}

public final class AppRuntimeCoordinator {
    public init(
        presenter: UIViewController,
        application: UIApplication,
        options: RuntimeCoordinatorOptions = RuntimeCoordinatorOptions()
    )
    public func start()
    public func ensureAll()
    public func stop()
}
```

Host: `AppDelegate` after `FlutterViewController` is the root view controller.
