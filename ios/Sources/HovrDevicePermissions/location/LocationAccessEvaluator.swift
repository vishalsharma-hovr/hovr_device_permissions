import CoreLocation

enum LocationAccessEvaluator {
    static func evaluate(
        locationServicesEnabled: Bool,
        authorization: CLAuthorizationStatus
    ) -> PermissionAccessIssue {
        if !locationServicesEnabled {
            return .deviceLocationDisabled
        }
        switch authorization {
        case .restricted:
            return .deviceLocationRestricted
        case .authorizedAlways, .authorizedWhenInUse:
            return .granted
        case .denied:
            return .appLocationDenied
        case .notDetermined:
            return .notDetermined
        @unknown default:
            return .appLocationDenied
        }
    }
}
