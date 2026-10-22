# Chano app

Open this folder in Android Studio or IntelliJ with Kotlin Multiplatform support.
Use JDK 21. The checked-in Gradle wrapper supplies Gradle 9.5.1.

## Targets and structure

| Target | Support | Launcher |
| --- | --- | --- |
| Android | Android 8+ (API 26); compile/target API 37 | `androidApp/` |
| iPhone / iPad | iOS 18.5+, ARM64 device and Apple Silicon simulator | `iosApp/iosApp.xcodeproj` |
| Desktop | macOS ARM64, Windows, Linux; package on the destination OS | `composeApp/src/desktopMain/` |
| Web | Kotlin/Wasm; browser with WasmGC support | `composeApp/src/wasmJsMain/` |

Shared UI, theme, API client, and API contract tests live in
`composeApp/src/commonMain/` and `commonTest/`. Android uses the dedicated KMP
library plugin with its application launcher in a separate module.

## Design system

- `ChanoTheme` uses **Material 3 Expressive** and its expressive motion scheme
  across the shared Compose UI. Android 12+ additionally uses system dynamic
  colors; older Android versions use the shared light/dark palette.
- Material 3 is pinned to `1.12.0-alpha03` because Expressive APIs ship on that
  track. Its experimental opt-in is isolated in `Theme.kt`.
- The SwiftUI iOS host renders the refresh control with Apple's native
  **Liquid Glass** API on iOS 26+. Earlier iOS versions use a system material;
  Reduce Transparency uses an opaque system surface. SwiftUI handles native
  glass interaction and system appearance; this is not a simulated blur.
- Android, JVM desktop (including macOS), and web currently use Expressive
  controls. They do not call Apple's native Liquid Glass APIs. Extending native
  glass to macOS would require a native AppKit/SwiftUI host or a separate shader
  implementation; the JVM launcher does not claim native glass support.

References: [Compose releases](https://github.com/JetBrains/compose-multiplatform/releases),
[Expressive theme](https://kotlinlang.org/docs/multiplatform/whats-new-compose-190.html#material-3-expressive-theme),
[Apple Liquid Glass](https://developer.apple.com/documentation/swiftui/view/glasseffect(_:in:)).

## Run

Start `../backend` first (port 3000).

```sh
# Desktop (macOS / Windows / Linux)
./gradlew :composeApp:run

# Android: configure sdk.dir in local.properties, or set ANDROID_HOME.
# Install Android SDK platform 37 and build-tools 36.0.0 or newer.
./gradlew :androidApp:assembleDebug
./gradlew :androidApp:installDebug  # requires an emulator/device

# Web dev server at http://localhost:8080
./gradlew :composeApp:wasmJsBrowserDevelopmentRun

# Web production bundle
./gradlew :composeApp:wasmJsBrowserDistribution

# Native desktop installer (build on each target OS)
./gradlew :composeApp:packageDistributionForCurrentOS
```

For iOS, open `iosApp/iosApp.xcodeproj`, select **Chano**, and run on an ARM64
simulator. Build with Xcode 26+ for Liquid Glass. A physical device requires your
own signing team, bundle identifier, and an API address reachable from the device.
Xcode's build phase builds and embeds the Kotlin framework automatically. Ensure
JDK 21 is available to Xcode (`JAVA_HOME` or `/usr/libexec/java_home`).

## Backend address

| Target | Development default | Override |
| --- | --- | --- |
| Android emulator | `http://10.0.2.2:3000` | Gradle `-PapiBaseUrl=https://your-api.example` |
| iOS simulator | `http://localhost:3000` | `CHANO_API_URL` in `iosApp/Local.xcconfig` |
| Desktop | `http://localhost:3000` | `CHANO_API_URL` environment variable |
| Web on localhost | `http://localhost:3000` | Set `window.CHANO_API_URL` before `composeApp.js` |
| Deployed web | Same origin as web app | Reverse proxy `/api/` or set `window.CHANO_API_URL` |

An API address is the server origin, without `/api/v1`. Android release and iOS
Release default to the placeholder `https://api.example.com`; replace it before
distribution. Use HTTPS for remote deployments. Only the Android debug manifest
permits cleartext HTTP. iOS permits local networking for development without
disabling ATS for remote hosts.

For a physical device, use your machine's LAN address. The backend binds to
`0.0.0.0` by default. For a separately hosted web client, add its exact origin to
the backend's `CORS_ORIGINS`.

Example local iOS override (ignored by version control):

```xcconfig
CHANO_API_URL = http:/$()/192.168.1.10:3000
```

## Verify

```sh
./gradlew :composeApp:desktopTest :composeApp:compileKotlinWasmJs :androidApp:assembleDebug
./gradlew :composeApp:linkDebugFrameworkIosSimulatorArm64
xcodebuild -project iosApp/iosApp.xcodeproj -scheme Chano \
  -configuration Debug -sdk iphonesimulator -destination 'generic/platform=iOS Simulator' \
  CODE_SIGNING_ALLOWED=NO build
```

Common tests check JSON decoding, endpoint construction, and failed responses
using Ktor's mock engine. Network requests have a 10-second timeout, propagate
coroutine cancellation, and close the client when the app leaves composition.

Pinned versions are in `gradle/libs.versions.toml`. The web toolchain's dependency
lockfile is checked in at `kotlin-js-store/wasm/yarn.lock`. The iOS deployment
target matches the minimum version used by the bundled Skiko ICU object.
