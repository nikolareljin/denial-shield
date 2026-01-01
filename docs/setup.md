# Setup Guide

This guide explains how to set up the environment for `denial-shield` development.

## Disclaimer

DenialShield does not provide legal, medical, or other advice to any patient or insurer. The app exists only to demonstrate on-device AI workflows and should not be used in real-world scenarios. Use at your own risk; the authors accept no responsibility or liability for any use of this software.

## Prerequisites

- Java 17 JDK
- Android Studio (optional, but recommended)
- Docker (for containerized builds and tests)
- Git

## Initial Setup

1. Clone the repository:
   ```bash
   git clone git@github.com:nikolareljin/denial-shield.git
   cd denial-shield
   ```

2. Initialize and update submodules:
   ```bash
   ./update
   ```

## Environment Variables

Ensure you have any necessary environment variables set in a `.env` file if required by the application.
