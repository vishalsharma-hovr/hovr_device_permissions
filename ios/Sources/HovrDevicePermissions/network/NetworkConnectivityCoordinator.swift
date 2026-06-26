import Network
import UIKit

final class NetworkConnectivityCoordinator {
    private let monitor = NWPathMonitor()
    private let queue = DispatchQueue(label: "com.hovr.devicepermissions.network")
    private let noConnectionAlert: NoConnectionAlert
    private var attached = false

    init(presenter: UIViewController) {
        noConnectionAlert = NoConnectionAlert(presenter: presenter)
    }

    func attach() {
        guard !attached else { return }
        attached = true
        monitor.pathUpdateHandler = { [weak self] path in
            DispatchQueue.main.async {
                if path.status == .satisfied {
                    self?.noConnectionAlert.dismiss()
                } else {
                    self?.noConnectionAlert.show { self?.recheck() }
                }
            }
        }
        monitor.start(queue: queue)
    }

    func detach() {
        guard attached else { return }
        attached = false
        monitor.cancel()
        noConnectionAlert.dismiss()
    }

    func recheck() {
        if monitor.currentPath.status == .satisfied {
            noConnectionAlert.dismiss()
        } else {
            noConnectionAlert.show { [weak self] in self?.recheck() }
        }
    }
}
