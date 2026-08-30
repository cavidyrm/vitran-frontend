# iOS Release Notes (Phase 12)

## Bundle / version

- Bundle ID: `com.vitran.shop.VitranShop` (+ TEAM_ID suffix per Config.xcconfig)
- `CURRENT_PROJECT_VERSION` / `MARKETING_VERSION` in `iosApp/Configuration/Config.xcconfig`
- Align marketing version with root `vitran.versionName` in `gradle.properties` (currently `1.0.0`)

## Signing

- `TEAM_ID` must be set externally (not committed with secrets)
- Certificates / provisioning profiles stay outside git
- Archive requires macOS + valid Apple Developer membership

## Keychain

- Production path: `IosSecureSessionStorage` via KVault → Keychain
- Verify Release builds use this path (not in-memory)

## Privacy usage strings

Declare only capabilities the app actually uses (photo picker / network). Do not invent legal copy.

## ATS

- Production API is HTTPS (`https://api.vitran.ir`)
- Do not disable ATS globally

## Universal Links

- Not declared for payment return (backend contract open — Gap 41)
- Association files are EXTERNAL when routes are verified

## Crash symbols

- CrashReporter is NoOp until EXTERNAL provider selected
- When enabled, upload dSYMs via provider workflow — never commit signing credentials

## Build

Produce Shared framework from Gradle (`iosArm64` / `iosSimulatorArm64` static framework baseName `Shared`) then open `iosApp` in Xcode.
