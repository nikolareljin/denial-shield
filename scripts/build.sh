#!/bin/bash
set -e

# Local build script for Android
echo "Building denial-shield locally..."

if [ ! -f "./gradlew" ]; then
    echo "gradlew not found. Attempting to initialize with gradle..."
    gradle wrapper
fi

./gradlew assembleDebug

echo "Build complete."
