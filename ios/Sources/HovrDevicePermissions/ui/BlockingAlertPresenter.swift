import UIKit

final class BlockingAlertPresenter {
    private weak var presenter: UIViewController?
    private var activeAlert: UIAlertController?
    private var activePriority: AlertPriority?

    init(presenter: UIViewController) {
        self.presenter = presenter
    }

    func showRequired(
        priority: AlertPriority,
        title: String,
        message: String,
        onRetry: @escaping () -> Void,
        openSettings: @escaping () -> Void
    ) {
        guard let presenter else { return }
        if let activePriority, activePriority.rawValue < priority.rawValue {
            return
        }
        dismiss()

        let alert = UIAlertController(title: title, message: message, preferredStyle: .alert)
        alert.addAction(UIAlertAction(title: "Retry", style: .default) { [weak self] _ in
            self?.dismiss()
            onRetry()
        })
        alert.addAction(UIAlertAction(title: "Open Settings", style: .default) { [weak self] _ in
            self?.dismiss()
            openSettings()
        })
        activePriority = priority
        activeAlert = alert
        presenter.present(alert, animated: true)
    }

    func dismissIfPriority(_ priority: AlertPriority) {
        if activePriority == priority {
            dismiss()
        }
    }

    func dismiss() {
        activeAlert?.dismiss(animated: true)
        activeAlert = nil
        activePriority = nil
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
