import CoreLocation
import UserNotifications

public enum PermissionStatusMapper {
    public static func locationStatus(from authorization: CLAuthorizationStatus) -> PermissionStatus {
        switch authorization {
        case .authorizedAlways, .authorizedWhenInUse:
            return .granted
        case .notDetermined:
            return .notDetermined
        case .denied:
            return .deniedPermanently
        case .restricted:
            return .restricted
        @unknown default:
            return .denied
        }
    }

    public static func notificationStatus(
        from authorization: UNAuthorizationStatus
    ) -> PermissionStatus {
        switch authorization {
        case .authorized, .provisional, .ephemeral:
            return .granted
        case .notDetermined:
            return .notDetermined
        case .denied:
            return .deniedPermanently
        @unknown default:
            return .denied
        }
    }

    public static func locationAccessIssue(
        locationServicesEnabled: Bool,
        authorization: CLAuthorizationStatus
    ) -> PermissionAccessIssue {
        LocationAccessEvaluator.evaluate(
            locationServicesEnabled: locationServicesEnabled,
            authorization: authorization
        )
    }

    public static func notificationAccessIssue(
        authorization: UNAuthorizationStatus
    ) -> PermissionAccessIssue {
        NotificationAccessEvaluator.evaluate(authorization: authorization)
    }
}
