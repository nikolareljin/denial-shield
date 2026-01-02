# Usage Guide

This guide explains how to use the automation scripts and the application.

## Disclaimer

DenialShield does not provide legal, medical, or other advice to any patient or insurer. The app exists only to demonstrate on-device AI workflows and should not be used in real-world scenarios. Use at your own risk; the authors accept no responsibility or liability for any use of this software.

## Automation Scripts

- `./update`: Updates git submodules.
- `./build`: Performs a local Android build.
- `./start`: Performs a Docker-based Android build.
- `./test`: Runs tests using Docker.

## Installing the App on a Phone

### Method 1: Using ADB (Developer Friendly)

1. **Enable Developer Options**: Go to **Settings > About Phone** and tap **Build Number** 7 times.
2. **Enable USB Debugging**: Go to **Settings > Developer Options** and toggle **USB Debugging** on.
3. **Connect Phone**: Connect your phone to your computer via USB.
4. **Install**:
   ```bash
   adb install app/build/outputs/apk/debug/app-debug.apk
   ```

### Method 2: Manual APK Transfer

1. **Build the APK**: Run `./build` in the project root.
2. **Transfer**: Copy `app/build/outputs/apk/debug/app-debug.apk` to your phone's storage.
3. **Open**: Use a File Manager on your phone to find and open the `.apk` file.
4. **Permissions**: If prompted, allow "Install from Unknown Sources".

## AI Model Setup

On first run, the app downloads a public LiteRT `.task` model (SmolLM-135M-Instruct) and stores it as `model.bin` under `Android/data/com.denialshield/files/models/`. This requires internet access and an ARM64 device/emulator to run the native GenAI runtime. If the model is missing or the runtime is unavailable, the app falls back to the template-based generator.

Manual override (optional): You can preinstall a model by copying it to `Android/data/com.denialshield/files/models/model.bin` on the device before launching the app.

For release and Play signing details, see the [Release Guide](release.md).
