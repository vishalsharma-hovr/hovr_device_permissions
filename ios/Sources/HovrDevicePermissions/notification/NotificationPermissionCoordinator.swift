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
                self?.handle(settings: settings)
            }
        }
    }

    @objc private func handleForeground() {
        ensureAccess()
    }

    private func handle(settings: UNNotificationSettings) {
        switch settings.authorizationStatus {
        case .authorized, .provisional, .ephemeral:
            alertPresenter.dismissIfPriority(.notification)
            application?.registerForRemoteNotifications()
        case .notDetermined:
            requestAuthorization()
        case .denied:
            showSettingsRequired()
        @unknown default:
            showSettingsRequired()
        }
    }

    private func requestAuthorization() {
        guard !hasRequestedAuthorization else {
            showSettingsRequired()
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
                    self?.showSettingsRequired()
                }
            }
        }
    }

    private func showSettingsRequired() {
        alertPresenter.showRequired(
            priority: .notification,
            title: "Notifications Required",
            message: "Please enable notifications to receive ride updates.",
            onRetry: { [weak self] in self?.ensureAccess() },
            openSettings: { SettingsNavigator.openNotificationSettings() }
        )
    }
}
