import Foundation

public enum PermissionStatus: Equatable {
    case granted
    case denied
    case deniedPermanently
    case notDetermined
    case restricted
    case serviceDisabled
}

public enum AlertPriority: Int {
    case network = 0
    case location = 1
    case notification = 2
}

public enum PermissionLimits {
    public static let notificationPromptDelayNanoseconds: UInt64 = 400_000_000
}
