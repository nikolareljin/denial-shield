# DenialShield: Automated Medical Denial Rebuttal Assistant

DenialShield is an Android application designed to help patients and caregivers generate clear, structured, and legally sound responses to medical claim denials. 

## Features

- **Step-by-step Denial Intake**: A guided workflow to capture claim and provider details.
- **AI-Powered Rebuttals**: Local-only processing using MediaPipe GenAI for private and professional response generation.
- **Document Processing**: OCR (ML Kit) for photos and PDF text extraction (PDFBox) to automatically extract policy language.
- **Privacy First**: All data is processed and stored locally on your device.
- **Export Options**: Share generated rebuttals via email, PDF, or secure messaging.

## Tech Stack

- **Jetpack Compose**: Modern UI implementation.
- **Room DB**: Local persistent storage.
- **MediaPipe GenAI**: Local LLM Inference for text generation.
- **ML Kit**: OCR for image text extraction.
- **PDFBox-Android**: PDF text processing.
- **Kotlin Coroutines & Flow**: Asynchronous processing and reactive UI.

## Getting Started

1. Clone this repository.
2. Open the project in **Android Studio (Ladybug or newer recommended)**.
3. Sync Project with Gradle Files.
4. **AI Generation**: For local AI to work, place a compatible `.bin` model (e.g., Gemma 2B) in the app's internal files directory. The app provides a fallback template generator if the model is missing.

## Installation

To install DenialShield on your Android phone:

1. **Build the APK**: Run `./build` or use Android Studio's **Build > Build Bundle(s) / APK(s) > Build APK(s)**.
2. **Locate the APK**: The file will be at `app/build/outputs/apk/debug/app-debug.apk`.
3. **Install**:
   - **Via ADB**: `adb install app/build/outputs/apk/debug/app-debug.apk`
   - **Manual Transfer**: Transfer the `.apk` file to your phone and open it with a file manager. You may need to enable "Install from Unknown Sources".

For detailed instructions, see the [Usage Guide](docs/usage.md).


## Privacy Note

DenialShield processes all medical information locally. No sensitive claim data or personal information is transmitted to any cloud servers for rebuttal generation.

## License

This project is licensed under a **Custom Proprietary License**.

- **Commercial Use**: Only the Creator (**Nikola Reljin**) is authorized to use this software for commercial purposes. All other commercial use is strictly prohibited.
- **Modifications**: No modifications are permitted without prior written consent from the Creator.

See the [LICENSE](LICENSE) file for the full legal terms.
