# Karate Android: Pure ADB Test Harness (NiA)

A high-performance, ultra-stable automated testing suite for the **Now in Android (NiA)** application, built using the **Karate DSL** and a custom **Pure ADB** architecture.

## 🚀 Overview

This project demonstrates a modern approach to Android UI testing that bypasses the traditional complexities and overhead of Appium. By leveraging direct ADB (Android Debug Bridge) commands and a lightweight Java interaction layer, we achieve near-instant execution speeds and significantly higher reliability.

### Key Metrics
*   **Speed**: ~50% faster than equivalent Appium-based suites.
*   **Stability**: 100% green build status across 7 core user flows.
*   **Zero-Dependency**: No Appium server, no Selenium grid, no Node.js required.

---

## 🏗️ Architecture: The "Pure ADB" Approach

The core of the framework is a custom **`AdbDriver.java`** that acts as the bridge between Karate and the Android device.

### 1. UI Interaction Layer
*   **UI Hierarchy Dumps**: Uses `uiautomator dump` to capture the screen state in XML.
*   **Jsoup Parsing**: Instead of heavy XPath engines, we use **Jsoup** to parse the XML hierarchy rapidly, identifying element coordinates via `bounds` attributes.
*   **Coordinate-Based Tapping**: Interacts with the device via `input tap` and `input swipe` based on the calculated center of elements.

### 2. Synchronization Strategy
*   **Logcat Polling**: Rather than arbitrary sleeps, the framework polls `logcat` for specific internal app events (e.g., `topic_followed`) to confirm state transitions.
*   **Wait-and-Retry Logic**: Implements aggressive retry loops for UI element detection, handling transient rendering states common in Jetpack Compose.

### 3. State Isolation
*   **App Resets**: Uses `pm clear` before every feature to ensure a deterministic start state.
*   **Permission Bypassing**: Automatically grants `POST_NOTIFICATIONS` permissions via ADB to prevent system dialogs from blocking the tests.
*   **Launcher Cleanup**: Concludes every scenario with `adb home` to return the device to a clean state.

---

## 🛠️ Overcoming Challenges

### 🛑 Issue: Flaky Locators in Jetpack Compose
*   **Solution**: Since Compose often obfuscates traditional IDs, we implemented a multi-match strategy (Text -> Content-Desc -> Resource-ID Suffix). For scrolling, we added `scrollUntilVisible()` which performs incremental swipes and UI dump checks until the target appears.

### 🛑 Issue: System Permission Popups
*   **Solution**: Android 13+ requires notification permissions on first launch. We integrated a pre-launch `pm grant` command into the `resetApp()` logic to handle this silently.

### 🛑 Issue: Browser Transition Detection
*   **Solution**: Verifying that a news item opens in Chrome is usually difficult. We implemented `waitForForegroundPackage()`, which polls the activity manager until the package name changes from `nowinandroid` to `com.android.chrome`.

---

## 📸 Visual Validation

Every major step in the suite triggers an automated screenshot using `adb.screenshot()`. These images are:
1.  Captured via `screencap`.
2.  Pulled to the local `target` folder.
3.  **Embedded directly** into the Karate HTML report for instant post-run verification.

---

## 🏃 Getting Started

### Prerequisites
*   **Android SDK**: ADB must be installed and the path updated in `AdbDriver.java`.
*   **Java 17+**
*   **Maven**

### Execution
Run the full suite with:
```powershell
mvn clean test
```

### Reports
View the detailed execution summary and screenshots at:
`target/karate-reports/karate-summary.html`

---

## 📋 Test Suite Coverage
1.  **App Launch**: Verified landing and title presence.
2.  **Bookmarking**: Validated item persistence in the Saved tab.
3.  **Comprehensive**: End-to-end onboarding and tab navigation.
4.  **Navigation**: Confirmed external browser transitions.
5.  **Propagation**: Validated topic follows from onboarding to Interests.
6.  **Sync**: Verified Interest toggle synchronization with the Feed.
7.  **Feed Follow**: Validated in-feed topic follows.

---

**Developed with ❤️ for stable Android automation.**
