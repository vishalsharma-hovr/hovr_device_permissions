import CoreLocation
import UIKit

final class LocationPermissionCoordinator: NSObject {
    private weak var presenter: UIViewController?
    private let alertPresenter: BlockingAlertPresenter
    private let locationManager = CLLocationManager()
    private var attached = false

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
        guard CLLocationManager.locationServicesEnabled() else {
            showGpsDisabled()
            return
        }
        handleAuthorizationStatus(locationManager.authorizationStatus)
    }

    @objc private func handleForeground() {
        ensureAccess()
    }

    private func handleAuthorizationStatus(_ status: CLAuthorizationStatus) {
        switch status {
        case .authorizedAlways, .authorizedWhenInUse:
            alertPresenter.dismissIfPriority(.location)
        case .notDetermined:
            locationManager.requestWhenInUseAuthorization()
        case .denied, .restricted:
            showSettingsRequired()
        @unknown default:
            showSettingsRequired()
        }
    }

    private func showGpsDisabled() {
        alertPresenter.showRequired(
            priority: .location,
            title: "Enable Location Services",
            message: "Location services are off. Turn them on to find nearby rides.",
            onRetry: { [weak self] in self?.ensureAccess() },
            openSettings: { SettingsNavigator.openAppSettings() }
        )
    }

    private func showSettingsRequired() {
        alertPresenter.showRequired(
            priority: .location,
            title: "Location Required",
            message: "Please enable location access to find nearby rides.",
            onRetry: { [weak self] in self?.ensureAccess() },
            openSettings: { SettingsNavigator.openAppSettings() }
        )
    }
}

extension LocationPermissionCoordinator: CLLocationManagerDelegate {
    func locationManagerDidChangeAuthorization(_ manager: CLLocationManager) {
        handleAuthorizationStatus(manager.authorizationStatus)
    }

    func locationManager(
        _ manager: CLLocationManager,
        didChangeAuthorization status: CLAuthorizationStatus
    ) {
        if #available(iOS 14.0, *) {
            return
        }
        handleAuthorizationStatus(status)
    }
}
