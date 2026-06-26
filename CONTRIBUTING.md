# Contributing

## Setup

```bash
cd native/hovr_device_permissions/android
./gradlew :hovr_device_permissions:testDebugUnitTest
```

## Before opening a PR

1. Android unit tests: `./gradlew :hovr_device_permissions:testDebugUnitTest`
2. Functions stay ≤ ~60 lines; split helpers when larger
3. No `exit(0)` / `exitProcess(0)` on deny or offline
4. Update `doc/PERMISSION_GUIDELINES.md` when changing permission flows
5. iOS: resolve Swift warnings in `HovrDevicePermissions` pod target

## Publishing

```bash
./tool/publish_to_github.sh v1.0.0
```

Requires `brew install gh && gh auth login`.
