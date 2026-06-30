import UIKit
import UserNotifications

final class NotificationPermissionCoordinator {
    private weak var presenter: UIViewController?
    private weak var application: UIApplication?
    private let alertPresenter: BlockingAlertPresenter
    private var attached = false
    private var hasRequestedAuthorization = false

    init(
        presenter: UIViewController,
        application: UIApplication,
        alertPresenter: BlockingAlertPresenter
    ) {
        self.presenter = presenter
        self.application = application
        self.alertPresenter = alertPresenter
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
        alertPresenter.dismissIfPriority(.notification)
    }

    func ensureAccess() {
        UNUserNotificationCenter.current().getNotificationSettings { [weak self] settings in
            DispatchQueue.main.async {
                self?.handleAccessIssue(
                    NotificationAccessEvaluator.evaluate(
                        authorization: settings.authorizationStatus
                    )
                )
            }
        }
    }

    @objc private func handleForeground() {
        ensureAccess()
    }

    private func handleAccessIssue(_ issue: PermissionAccessIssue) {
        switch PermissionActionResolver.resolveNotificationAction(issue: issue) {
        case .dismiss:
            alertPresenter.dismissIfPriority(.notification)
            application?.registerForRemoteNotifications()
        case .requestSystem:
            requestAuthorization()
        case .showRequired:
            showAppPermissionRequired()
        case .showDeviceLocationDisabled, .showDeviceLocationRestricted:
            break
        }
    }

    private func showAppPermissionRequired() {
        alertPresenter.showRequired(
            priority: .notification,
            reasonKey: PermissionAlertReason.appNotificationDenied,
            title: "Notifications Permission Required",
            message: "Allow Hovr to send notifications in app settings.",
            settingsDestination: .appNotifications,
            onRetry: { [weak self] in self?.retryAccess() },
            onStillRequired: { [weak self] in self?.ensureAccess() }
        )
    }

    private func retryAccess() {
        UNUserNotificationCenter.current().getNotificationSettings { [weak self] settings in
            DispatchQueue.main.async {
                guard let self else { return }
                let issue = NotificationAccessEvaluator.evaluate(
                    authorization: settings.authorizationStatus
                )
                switch PermissionActionResolver.resolveNotificationAction(issue: issue) {
                case .dismiss:
                    self.alertPresenter.dismissIfPriority(.notification)
                    self.application?.registerForRemoteNotifications()
                case .requestSystem:
                    self.requestAuthorization()
                case .showRequired:
                    self.ensureAccess()
                case .showDeviceLocationDisabled, .showDeviceLocationRestricted:
                    break
                }
            }
        }
    }

    private func requestAuthorization() {
        guard !hasRequestedAuthorization else {
            return
        }
        hasRequestedAuthorization = true
        UNUserNotificationCenter.current().requestAuthorization(
            options: [.alert, .badge, .sound]
        ) { [weak self] granted, _ in
            DispatchQueue.main.async {
                if granted {
                    self?.alertPresenter.dismissIfPriority(.notification)
                    self?.application?.registerForRemoteNotifications()
                } else {
                    self?.ensureAccess()
                }
            }
        }
    }
}
