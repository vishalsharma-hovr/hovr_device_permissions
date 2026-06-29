import Foundation

public struct RuntimeCoordinatorOptions {
    public let monitorLocation: Bool
    public let monitorNotifications: Bool
    public let monitorNetwork: Bool

    public init(
        monitorLocation: Bool = true,
        monitorNotifications: Bool = true,
        monitorNetwork: Bool = true
    ) {
        self.monitorLocation = monitorLocation
        self.monitorNotifications = monitorNotifications
        self.monitorNetwork = monitorNetwork
    }
}
