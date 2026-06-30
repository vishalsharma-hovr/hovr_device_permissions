import CoreLocation
import UIKit

final class LocationPermissionCoordinator: NSObject {
    private weak var presenter: UIViewController?
    private let alertPresenter: BlockingAlertPresenter
    private let locationManager = CLLocationManager()
    private var attached = false
    private var pendingSystemRequest = false

    init(presenter: UIViewController, alertPresenter: BlockingAlertPresenter) {
        self.presenter = presenter
        self.alertPresenter = alertPresenter
        super.init()
        locationManager.delegate = self
    }

    func attach() {
        guard !attached else { return }
        attached = true
        NotificationCenter.default.addObserver(
            self,
            selector: #selector(handleForeground),
            name: UIApplication.willEnterForegroundNotification,
            object: nil
        )
    }

    func detach() {
        guard attached else { return }
        attached = false
        NotificationCenter.default.removeObserver(self)
        alertPresenter.dismissIfPriority(.location)
    }

    func ensureAccess() {
        let issue = LocationAccessEvaluator.evaluate(
            locationServicesEnabled: CLLocationManager.locationServicesEnabled(),
            authorization: locationManager.authorizationStatus
        )
        handleAccessIssue(issue)
    }

    @objc private func handleForeground() {
        ensureAccess()
    }

    private func handleAccessIssue(_ issue: PermissionAccessIssue) {
        switch PermissionActionResolver.resolveLocationAction(issue: issue) {
        case .dismiss:
            alertPresenter.dismissIfPriority(.location)
        case .showDeviceLocationDisabled:
            showDeviceLocationDisabled()
        case .showDeviceLocationRestricted:
            showDeviceLocationRestricted()
        case .requestSystem:
            requestSystemPermission()
        case .showRequired:
            showAppPermissionRequired()
        }
    }

    private func requestSystemPermission() {
        guard !pendingSystemRequest else { return }
        pendingSystemRequest = true
        locationManager.requestWhenInUseAuthorization()
    }

    private func showDeviceLocationDisabled() {
        alertPresenter.showRequired(
            priority: .location,
            reasonKey: PermissionAlertReason.deviceLocationDisabled,
            title: "Enable Location Services",
            message: "Location is turned off on this device. Go to Settings > Privacy & Security > Location Services and turn Location Services on.",
            settingsDestination: .deviceLocationInstructions,
            onRetry: { [weak self] in self?.retryAccess() },
            onStillRequired: { [weak self] in self?.ensureAccess() }
        )
    }

    private func showDeviceLocationRestricted() {
        alertPresenter.showRequired(
            priority: .location,
            reasonKey: PermissionAlertReason.deviceLocationRestricted,
            title: "Location Restricted",
            message: "Location access is restricted on this device. Check Screen Time or device management settings.",
            settingsDestination: .deviceRestrictionInstructions,
            onRetry: { [weak self] in self?.retryAccess() },
            onStillRequired: { [weak self] in self?.ensureAccess() }
        )
    }

    private func showAppPermissionRequired() {
        alertPresenter.showRequired(
            priority: .location,
            reasonKey: PermissionAlertReason.appLocationDenied,
            title: "Location Permission Required",
            message: "Allow Hovr to access your location in app settings.",
            settingsDestination: .appLocation,
            onRetry: { [weak self] in self?.retryAccess() },
            onStillRequired: { [weak self] in self?.ensureAccess() }
        )
    }

    private func retryAccess() {
        let issue = LocationAccessEvaluator.evaluate(
            locationServicesEnabled: CLLocationManager.locationServicesEnabled(),
            authorization: locationManager.authorizationStatus
        )
        switch PermissionActionResolver.resolveLocationAction(issue: issue) {
        case .dismiss:
            alertPresenter.dismissIfPriority(.location)
        case .showDeviceLocationDisabled, .showDeviceLocationRestricted, .showRequired:
            ensureAccess()
        case .requestSystem:
            requestSystemPermission()
        }
    }
}

extension LocationPermissionCoordinator: CLLocationManagerDelegate {
    func locationManagerDidChangeAuthorization(_ manager: CLLocationManager) {
        pendingSystemRequest = false
        ensureAccess()
    }

    func locationManager(
        _ manager: CLLocationManager,
        didChangeAuthorization status: CLAuthorizationStatus
    ) {
        if #available(iOS 14.0, *) {
            return
        }
        pendingSystemRequest = false
        ensureAccess()
    }
}
