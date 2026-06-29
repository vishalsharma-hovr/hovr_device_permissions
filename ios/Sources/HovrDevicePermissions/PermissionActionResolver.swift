import Foundation

public enum PermissionCoordinatorAction: Equatable {
    case dismiss
    case requestSystem
    case showRequired
    case showDeviceLocationDisabled
    case showDeviceLocationRestricted
}

public enum PermissionActionResolver {
    public static func resolveLocationAction(
        issue: PermissionAccessIssue
    ) -> PermissionCoordinatorAction {
        switch issue {
        case .granted:
            return .dismiss
        case .deviceLocationDisabled:
            return .showDeviceLocationDisabled
        case .deviceLocationRestricted:
            return .showDeviceLocationRestricted
        case .notDetermined:
            return .requestSystem
        case .appLocationDenied:
            return .showRequired
        case .appNotificationDenied:
            return .dismiss
        }
    }

    public static func resolveNotificationAction(
        issue: PermissionAccessIssue
    ) -> PermissionCoordinatorAction {
        switch issue {
        case .granted:
            return .dismiss
        case .notDetermined:
            return .requestSystem
        case .appNotificationDenied:
            return .showRequired
        case .deviceLocationDisabled,
             .deviceLocationRestricted,
             .appLocationDenied:
            return .dismiss
        }
    }
}
