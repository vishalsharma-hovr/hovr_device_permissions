// swift-tools-version: 5.9
import PackageDescription

let package = Package(
    name: "HovrDevicePermissions",
    platforms: [
        .iOS(.v16),
    ],
    products: [
        .library(
            name: "HovrDevicePermissions",
            targets: ["HovrDevicePermissions"]
        ),
    ],
    targets: [
        .target(
            name: "HovrDevicePermissions",
            path: "ios/Sources/HovrDevicePermissions"
        ),
    ]
)
