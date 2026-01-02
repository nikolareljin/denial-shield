#!/usr/bin/env bash
# SCRIPT: scripts/build.sh
# DESCRIPTION: Build debug or release APKs via Gradle.
# USAGE: ./scripts/build.sh [-h] [-d | -r]
# PARAMETERS:
#   -d           Build a debug APK (assembleDebug).
#   -r           Build a release APK (assembleRelease).
#   -h, --help   Show this help message.
# EXAMPLE:
#   ./scripts/build.sh -r
# EXIT_CODES:
#   0  Success.
#   1  Invalid arguments or build failure.
set -euo pipefail

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
INCLUDE_PATH=""
for candidate in "$SCRIPT_DIR/include.sh" "$SCRIPT_DIR/scripts/include.sh"; do
  if [[ -f "$candidate" ]]; then
    INCLUDE_PATH="$candidate"
    break
  fi
done

if [[ -z "$INCLUDE_PATH" ]]; then
  echo "[Error!] include.sh not found. Tried $SCRIPT_DIR/include.sh and $SCRIPT_DIR/scripts/include.sh" >&2
  exit 1
fi

# shellcheck source=/dev/null
source "$INCLUDE_PATH"
shlib_import logging env help

show_help() {
  print_help "$0"
}

build_task="assembleDebug"
case "${1:-}" in
  -h|--help)
    show_help
    exit 0
    ;;
  -d)
    build_task="assembleDebug"
    ;;
  -r)
    build_task="assembleRelease"
    ;;
  "" ) ;;
  *)
    print_error "Unknown option: $1"
    show_help
    exit 1
    ;;
esac

cd "$PROJECT_ROOT"
load_env

print_info "Building denial-shield locally ($build_task)..."

if [[ ! -x "./gradlew" ]]; then
  print_warning "gradlew not found. Attempting to initialize with gradle wrapper..."
  gradle wrapper
fi

./gradlew "$build_task"

print_success "Build complete."
