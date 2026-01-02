#!/usr/bin/env bash
# SCRIPT: scripts/update.sh
# DESCRIPTION: Update git submodules and refresh Gradle dependencies.
# USAGE: ./scripts/update.sh [-h]
# PARAMETERS:
#   -h, --help   Show this help message.
# EXAMPLE:
#   ./scripts/update.sh
# EXIT_CODES:
#   0  Success.
#   1  Invalid arguments or update failure.
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

case "${1:-}" in
  -h|--help)
    show_help
    exit 0
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

print_info "Updating git submodules..."
git submodule update --init --recursive

if [[ ! -x "./gradlew" ]]; then
  print_warning "gradlew not found. Attempting to initialize with gradle wrapper..."
  gradle wrapper
fi

print_info "Refreshing Gradle dependencies..."
./gradlew --refresh-dependencies

print_success "Update complete."
