# Setup Guide

This guide explains how to set up the environment for `denial-shield` development.

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
