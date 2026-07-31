# Release Guide

## Google Play Signing

When you are ready to publish, use **Play App Signing**:

1. Create a Play Console account and an app entry.
2. In **Release > Setup > App integrity**, enable **Play App Signing**.
3. Use your existing release keystore as the **upload key** (or generate a new upload key if you prefer).
4. Build a signed release APK/AAB locally (`./gradlew assembleRelease` or `./gradlew bundleRelease`).
5. Upload the signed artifact in the Play Console.

Notes:
- Keep your keystore backed up; you will need it to sign future uploads.
- Play will manage the **app signing key** once enabled.
