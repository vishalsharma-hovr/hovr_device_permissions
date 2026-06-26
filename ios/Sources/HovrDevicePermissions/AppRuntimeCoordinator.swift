import UIKit

public final class AppRuntimeCoordinator {
    private weak var presenter: UIViewController?
    private let application: UIApplication
    private let alertPresenter: BlockingAlertPresenter
    private let networkCoordinator: NetworkConnectivityCoordinator
    private let locationCoordinator: LocationPermissionCoordinator
    private let notificationCoordinator: NotificationPermissionCoordinator
    private var started = false

    public init(presenter: UIViewController, application: UIApplication) {
        self.presenter = presenter
        self.application = application
        alertPresenter = BlockingAlertPresenter(presenter: presenter)
        networkCoordinator = NetworkConnectivityCoordinator(presenter: presenter)
        locationCoordinator = LocationPermissionCoordinator(
            presenter: presenter,
            alertPresenter: alertPresenter
        )
        notificationCoordinator = NotificationPermissionCoordinator(
            presenter: presenter,
            application: application,
            alertPresenter: alertPresenter
        )
    }

    public func start() {
        guard !started else { return }
        started = true
        networkCoordinator.attach()
        locationCoordinator.attach()
        notificationCoordinator.attach()
    }

    public func ensureAll() {
        networkCoordinator.recheck()
        locationCoordinator.ensureAccess()
        Task {
            try? await Task.sleep(nanoseconds: PermissionLimits.notificationPromptDelayNanoseconds)
            await MainActor.run { [weak self] in
                self?.notificationCoordinator.ensureAccess()
            }
        }
    }

    public func stop() {
        guard started else { return }
        started = false
        notificationCoordinator.detach()
        locationCoordinator.detach()
        networkCoordinator.detach()
        alertPresenter.dismiss()
    }
}
