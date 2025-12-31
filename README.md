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

## Privacy Note

DenialShield processes all medical information locally. No sensitive claim data or personal information is transmitted to any cloud servers for rebuttal generation.
