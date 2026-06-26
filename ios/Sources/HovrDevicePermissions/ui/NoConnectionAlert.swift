import UIKit

final class NoConnectionAlert {
    private weak var presenter: UIViewController?
    private var activeAlert: UIAlertController?

    init(presenter: UIViewController) {
        self.presenter = presenter
    }

    func show(onRetry: @escaping () -> Void) {
        guard let presenter else { return }
        if activeAlert != nil { return }

        let alert = UIAlertController(
            title: "No Internet Connection",
            message: "Please check your network and try again.",
            preferredStyle: .alert
        )
        alert.addAction(UIAlertAction(title: "Retry", style: .default) { [weak self] _ in
            self?.dismiss()
            onRetry()
        })
        activeAlert = alert
        presenter.present(alert, animated: true)
    }

    func dismiss() {
        activeAlert?.dismiss(animated: true)
        activeAlert = nil
    }
}
