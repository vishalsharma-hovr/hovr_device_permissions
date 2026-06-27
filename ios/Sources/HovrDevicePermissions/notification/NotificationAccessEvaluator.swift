import UserNotifications

enum NotificationAccessEvaluator {
    static func evaluate(authorization: UNAuthorizationStatus) -> PermissionAccessIssue {
        switch authorization {
        case .authorized, .provisional, .ephemeral:
            return .granted
        case .notDetermined:
            return .notDetermined
        case .denied:
            return .appNotificationDenied
        @unknown default:
            return .appNotificationDenied
        }
    }
}
