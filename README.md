# Galaxy Watch Weather Fix

A Wear OS app that forces weather refresh on Samsung Galaxy Watch devices.

## What it does

Samsung Galaxy Watch weather widgets sometimes stop updating automatically. This app works around the issue by periodically sending a broadcast intent to Samsung's weather app (`com.samsung.android.watch.weather`) to trigger refresh cycles.

## Features

- **Manual refresh**: Send a refresh broadcast immediately
- **Scheduled refresh**: Set up a repeating alarm (every 30 minutes) to automatically refresh weather
- **Time range**: Configure a time window (e.g., 8:00 to 23:00) during which automatic refreshes are active

## Downloads

Releases are available in the [GitHub Releases](https://github.com/kmod-midori/GalaxyWatchWeatherFix/releases) section.

## Requirements

- Samsung Galaxy Watch running Wear OS
- Android 14 (API 34) or later
