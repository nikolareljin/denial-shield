# CI Guide

This guide explains the Continuous Integration (CI) setup for `denial-shield`.

## Disclaimer

DenialShield does not provide legal, medical, or other advice to any patient or insurer. The app exists only to demonstrate on-device AI workflows and should not be used in real-world scenarios. Use at your own risk; the authors accept no responsibility or liability for any use of this software.

## GitHub Actions

The project uses GitHub Actions for CI. The workflow is triggered on every push and pull request to the `main` branch.

### Workflow Steps

1. **Checkout Code**: Checks out the repository and initializes submodules.
2. **Setup Java**: Sets up Java 17.
3. **Build**: Runs `./gradlew assembleDebug` to ensure the app builds.
4. **Test**: Runs `./gradlew test` to ensure all tests pass.

## Local CI Testing

You can simulate the CI environment locally using the Docker-based scripts:

```bash
./start  # Builds the app in Docker
./test   # Runs tests in Docker
```
