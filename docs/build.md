# Build Guide

This guide explains how to build the `denial-shield` application.

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
