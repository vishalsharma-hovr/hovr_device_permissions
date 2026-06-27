import Foundation

public enum PermissionAccessIssue: Equatable {
    case granted
    case deviceLocationDisabled
    case deviceLocationRestricted
    case appLocationDenied
    case appNotificationDenied
    case notDetermined
}
