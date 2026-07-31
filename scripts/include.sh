#!/usr/bin/env bash
# SCRIPT: scripts/include.sh
# DESCRIPTION: Bootstrap script-helpers and shared shell setup.
# USAGE: source ./scripts/include.sh
# PARAMETERS:
#   None (intended to be sourced).
# EXAMPLE:
#   source "./scripts/include.sh"
# EXIT_CODES:
#   0  Success.
#   1  Failed to initialize script-helpers.

set -euo pipefail

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PROJECT_ROOT="$(cd "$SCRIPT_DIR/.." && pwd)"
SCRIPT_HELPERS_DIR="${SCRIPT_HELPERS_DIR:-$SCRIPT_DIR/script-helpers}"

ensure_script_helpers() {
  if [[ -f "$SCRIPT_HELPERS_DIR/helpers.sh" ]]; then
    return 0
  fi

  if [[ -f "$PROJECT_ROOT/.gitmodules" ]]; then
    git -C "$PROJECT_ROOT" submodule update --init --recursive scripts/script-helpers || true
  fi

  if [[ ! -f "$SCRIPT_HELPERS_DIR/helpers.sh" ]]; then
    echo "[Error!] script-helpers not found at $SCRIPT_HELPERS_DIR" >&2
    return 1
  fi
}

ensure_script_helpers

# shellcheck source=/dev/null
source "$SCRIPT_HELPERS_DIR/helpers.sh"

if [[ -d "$PROJECT_ROOT/.git" ]]; then
  export PROJECT_ROOT
else
  export PROJECT_ROOT="$SCRIPT_DIR"
fi
