#!/bin/bash
set -e

# Docker-based test script
echo "Running tests for denial-shield using Docker..."

docker build -t denial-shield-tester -f scripts/Dockerfile.test .
docker run --rm -v $(pwd):/app denial-shield-tester

echo "Tests complete."
