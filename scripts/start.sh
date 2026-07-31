#!/bin/bash
set -e

# Docker-based build script
echo "Building denial-shield using Docker..."

docker build -t denial-shield-builder -f scripts/Dockerfile.build .
docker run --rm -v $(pwd):/app denial-shield-builder

echo "Docker build complete."
