import UIKit

enum SettingsDestination {
    case appLocation
    case appNotifications
    case deviceLocationInstructions
    case deviceRestrictionInstructions

    var secondaryButtonTitle: String {
        switch self {
        case .appLocation:
            return "Open App Settings"
        case .appNotifications:
            return "Open Notification Settings"
        case .deviceLocationInstructions, .deviceRestrictionInstructions:
            return "Got it"
        }
    }

    func perform() {
        switch self {
        case .appLocation:
            SettingsNavigator.openAppSettings()
        case .appNotifications:
            SettingsNavigator.openNotificationSettings()
        case .deviceLocationInstructions, .deviceRestrictionInstructions:
            break
        }
    }
}

final class BlockingAlertPresenter {
    private weak var presenter: UIViewController?
    private var activeAlert: UIAlertController?
    private var activePriority: AlertPriority?
    private var activeReasonKey: String?

    init(presenter: UIViewController) {
        self.presenter = presenter
    }

    func showRequired(
        priority: AlertPriority,
        reasonKey: String,
        title: String,
        message: String,
        settingsDestination: SettingsDestination,
        onRetry: @escaping () -> Void,
        onStillRequired: @escaping () -> Void
    ) {
        guard let presenter else { return }
        if activePriority == priority,
           activeReasonKey == reasonKey,
           let activeAlert,
           presenter.presentedViewController === activeAlert {
            return
        }
        if let activePriority, activePriority.rawValue < priority.rawValue {
            return
        }
        dismissInternal()

        let alert = UIAlertController(title: title, message: message, preferredStyle: .alert)
        alert.isModalInPresentation = true
        alert.addAction(UIAlertAction(title: "Retry", style: .default) { [weak self] _ in
            onRetry()
            self?.scheduleReshowIfNeeded(
                priority: priority,
                onStillRequired: onStillRequired
            )
        })
        alert.addAction(UIAlertAction(
            title: settingsDestination.secondaryButtonTitle,
            style: .default
        ) { [weak self] _ in
            settingsDestination.perform()
            self?.scheduleReshowIfNeeded(
                priority: priority,
                onStillRequired: onStillRequired
            )
        })
        activePriority = priority
        activeReasonKey = reasonKey
        activeAlert = alert
        presenter.present(alert, animated: true)
    }

    func dismissIfPriority(_ priority: AlertPriority) {
        if activePriority == priority {
            dismissInternal()
        }
    }

    func dismiss() {
        dismissInternal()
    }

    private func dismissInternal() {
        activeAlert?.dismiss(animated: true)
        activeAlert = nil
        activePriority = nil
        activeReasonKey = nil
    }

    private func scheduleReshowIfNeeded(
        priority: AlertPriority,
        onStillRequired: @escaping () -> Void
    ) {
        DispatchQueue.main.asyncAfter(deadline: .now() + 0.15) { [weak self] in
            guard let self, let presenter = self.presenter else { return }
            if presenter.presentedViewController === self.activeAlert {
                return
            }
            guard self.activePriority == priority else {
                return
            }
            self.activeAlert = nil
            onStillRequired()
        }
    }
}

enum SettingsNavigator {
    static func openAppSettings() {
        guard let url = URL(string: UIApplication.openSettingsURLString) else { return }
        UIApplication.shared.open(url)
    }

    static func openNotificationSettings() {
        if #available(iOS 16.0, *) {
            if let url = URL(string: UIApplication.openNotificationSettingsURLString) {
                UIApplication.shared.open(url)
                return
            }
        }
        openAppSettings()
    }
}
