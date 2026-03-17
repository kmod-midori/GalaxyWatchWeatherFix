# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

Galaxy Watch Weather Fix is a Wear OS application that forces weather refresh on Samsung Galaxy Watch devices. It works by sending broadcast intents to Samsung's weather app (`com.samsung.android.watch.weather`) to trigger automatic refresh cycles.

## Build Commands

```bash
# Build debug APK
./gradlew assembleDebug

# Build release APK
./gradlew assembleRelease

# Clean build
./gradlew clean

# Run lint checks
./gradlew lint

# Install debug on connected device
./gradlew installDebug
```

## Architecture

### Core Components

- **WeatherFixApplication**: Application class that initializes SharedPreferences state and exposes a `lastAlarm` StateFlow for tracking refresh timestamps.

- **AlarmReceiver**: BroadcastReceiver that handles:
  - `BOOT_COMPLETED` - Reschedules the repeating alarm after device reboot
  - `AUTO_REFRESH` action - Sends the weather refresh broadcast to Samsung's weather app

- **MainActivity**: Wear OS Compose UI with two actions:
  - Manual "Send refresh broadcast" button
  - "Schedule alarm" button to set up 30-minute repeating refresh cycles

### Key Implementation Details

The app sends a broadcast with action `com.samsung.android.weather.intent.action.AUTOREFRESH` to package `com.samsung.android.watch.weather`. This is Samsung's internal weather app on Galaxy Watch devices.

Alarms use `AlarmManager.setInexactRepeating()` with a 30-minute interval, starting 10 seconds after scheduling.

### UI Stack

- Jetpack Compose for Wear OS with Material 3
- `TransformingLazyColumn` for scrollable content
- Wear OS preview annotations (`@WearPreviewDevices`, `@WearPreviewFontScales`)

## Configuration

- **minSdk**: 34 (Android 14)
- **targetSdk**: 36
- **Namespace**: `moe.reimu.galaxy.weather.fix`
- Uses `wear-sdk` library for Wear OS-specific APIs
