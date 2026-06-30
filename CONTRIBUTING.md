# Contributing

## Workflow

1. **Develop** in `packages/native/hovr_device_permissions/` (rider) or a cloned copy in driver.
2. **Test locally** — temporarily wire local Gradle / CocoaPods path in the host app. See [doc/LOCAL_DEVELOPMENT.md](doc/LOCAL_DEVELOPMENT.md).
3. **Publish** only after tests pass: `./tool/publish_to_github.sh v1.x.x`
4. **Trigger JitPack** for the new Android tag, then bump rider/driver to remote coordinates.
5. **Never merge** local `project(':hovr_device_permissions')` or `:path` pods into rider/driver `main`.

## Setup

```bash
cd packages/native/hovr_device_permissions
./gradlew :hovr_device_permissions:testDebugUnitTest
```

## Before opening a PR (module repo)

1. Android unit tests: `./gradlew :hovr_device_permissions:testDebugUnitTest`
2. Functions stay ≤ ~60 lines; split helpers when larger
3. No `exit(0)` / `exitProcess(0)` on deny or offline
4. Update `doc/PERMISSION_GUIDELINES.md` when changing permission flows
5. iOS: resolve Swift warnings in `HovrDevicePermissions` pod target
6. Update `doc/INTEGRATION.md` current release table after publish

## Publishing

```bash
./tool/publish_to_github.sh v1.2.9
```

Requires `brew install gh && gh auth login` as `vishalsharma-hovr`.

After publish:

1. Open `https://jitpack.io/#vishalsharma-hovr/hovr_device_permissions/v1.2.9` and confirm build succeeds.
2. Bump rider/driver `android/app/build.gradle` and `ios/Podfile` to the new tag.
3. Revert any local-path testing wiring in host apps.
