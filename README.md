# DenialShield: Automated Medical Denial Rebuttal Assistant

DenialShield is an Android application designed to help patients and caregivers generate clear, structured, and legally sound responses to medical claim denials. 

## Disclaimer

DenialShield does not provide legal, medical, or other advice to any patient or insurer. The app exists only to demonstrate on-device AI workflows and should not be used in real-world scenarios. Use at your own risk; the authors accept no responsibility or liability for any use of this software.

## Features

- **Step-by-step Denial Intake**: A guided workflow to capture claim and provider details.
- **AI-Powered Rebuttals**: Local-only processing using MediaPipe GenAI for private and professional response generation.
- **Document Processing**: OCR (ML Kit) for photos and PDF text extraction (PDFBox) to automatically extract policy language.
- **Privacy First**: All data is processed and stored locally on your device.
- **Export Options**: Share generated rebuttals via email, PDF, or secure messaging.

## Usage

- Open the app

<img width="1080" height="2220" alt="Screenshot_20251231_185021" src="https://github.com/user-attachments/assets/e8ad1841-b702-458d-ae2e-0841b5b9c242" />

- Enter insurance information and personal details

<img width="1080" height="2220" alt="Screenshot_20251231_185603" src="https://github.com/user-attachments/assets/8e97ee36-f890-4769-8feb-ea08c31cbb5f" />

- Enter the denied claim

<img width="1080" height="2220" alt="Screenshot_20251231_185752" src="https://github.com/user-attachments/assets/fbab7883-01b1-4f5d-89a0-32eea859ef4a" />

- Enter basic information about the claim, take the photo or upload the document

<img width="1080" height="2220" alt="Screenshot_20251231_185833" src="https://github.com/user-attachments/assets/f2a2dd20-1c09-405a-99fd-3714a865a33f" />

<img width="1080" height="2220" alt="Screenshot_20251231_193510" src="https://github.com/user-attachments/assets/2ba882d5-5890-48ac-9c2c-d0a06a5d4a5b" />

- Processing denial documents

<img width="1080" height="2220" alt="Screenshot_20251231_193519" src="https://github.com/user-attachments/assets/6e085b18-8c48-45b7-8f24-b5e107caf159" />


- Generating response

- Submit the rebuttal


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
