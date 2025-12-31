# Usage Guide

This guide explains how to use the automation scripts and the application.

## Automation Scripts

- `./update`: Updates git submodules.
- `./build`: Performs a local Android build.
- `./start`: Performs a Docker-based Android build.
- `./test`: Runs tests using Docker.

## Running the App

After building, the APK can be found in `app/build/outputs/apk/debug/`.
You can install it on an Android device or emulator using `adb`:

```bash
adb install app/build/outputs/apk/debug/app-debug.apk
```
