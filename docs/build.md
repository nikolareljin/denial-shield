# Build Guide

This guide explains how to build the `denial-shield` application.

## Disclaimer

DenialShield does not provide legal, medical, or other advice to any patient or insurer. The app exists only to demonstrate on-device AI workflows and should not be used in real-world scenarios. Use at your own risk; the authors accept no responsibility or liability for any use of this software.

## Local Build

If you have Java 17 and Gradle installed, you can build locally:

```bash
./build
```

This runs `./scripts/build.sh` which executes `./gradlew assembleDebug`.

## Docker-based Build

To build without installing dependencies locally:

```bash
./start
```

This runs `./scripts/start.sh` which builds a Docker image and runs the build process inside a container.
