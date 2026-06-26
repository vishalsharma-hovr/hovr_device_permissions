# Coding standards

| # | Rule | Enforcement |
|---|------|-------------|
| 1 | Simple control flow | Early-return; no nested `if` deeper than 3 |
| 2 | Bounded loops | Lifecycle/delegate callbacks only; `PermissionLimits` for delays |
| 3 | Limit allocation | Reuse dialog instances per presenter |
| 4 | Functions ≤ ~60 lines | Split `*Checker`, `*Dialog`, `*Coordinator` files |
| 5 | Assertions & tests | JUnit on Android status mapping |
| 6 | Smallest scope | Coordinator owned by Activity/AppDelegate |
| 7 | Check return values | All permission/network callbacks handled |
| 8 | Minimal API guards | SDK 33+ checks in `NotificationPermissionChecker` only |
| 9 | Reference safety | `weak` presenter refs; `guard let` in Swift |
| 10 | Zero warnings | Clean Gradle tests; no suppressed lint without comment |

Forbidden: `exit(0)`, `exitProcess(0)`, `finishAffinity()` on deny/offline.
