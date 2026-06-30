import UIKit

public final class AppRuntimeCoordinator {
    private weak var presenter: UIViewController?
    private let application: UIApplication
    private let options: RuntimeCoordinatorOptions
    private let alertPresenter: BlockingAlertPresenter
    private let networkCoordinator: NetworkConnectivityCoordinator?
    private let locationCoordinator: LocationPermissionCoordinator?
    private let notificationCoordinator: NotificationPermissionCoordinator?
    private var started = false

    public init(
        presenter: UIViewController,
        application: UIApplication,
        options: RuntimeCoordinatorOptions = RuntimeCoordinatorOptions()
    ) {
        self.presenter = presenter
        self.application = application
        self.options = options
        alertPresenter = BlockingAlertPresenter(presenter: presenter)
        networkCoordinator = options.monitorNetwork
            ? NetworkConnectivityCoordinator(presenter: presenter)
            : nil
        locationCoordinator = options.monitorLocation
            ? LocationPermissionCoordinator(
                presenter: presenter,
                alertPresenter: alertPresenter
            )
            : nil
        notificationCoordinator = options.monitorNotifications
            ? NotificationPermissionCoordinator(
                presenter: presenter,
                application: application,
                alertPresenter: alertPresenter
            )
            : nil
    }

    public func start() {
        guard !started else { return }
        started = true
        networkCoordinator?.attach()
        locationCoordinator?.attach()
        notificationCoordinator?.attach()
    }

    public func ensureAll() {
        networkCoordinator?.recheck()
        locationCoordinator?.ensureAccess()
        guard notificationCoordinator != nil else { return }
        Task {
            try? await Task.sleep(nanoseconds: PermissionLimits.notificationPromptDelayNanoseconds)
            await MainActor.run { [weak self] in
                self?.notificationCoordinator?.ensureAccess()
            }
        }
    }

    public func stop() {
        guard started else { return }
        started = false
        notificationCoordinator?.detach()
        locationCoordinator?.detach()
        networkCoordinator?.detach()
        alertPresenter.dismiss()
    }
}
