#!/usr/bin/env bash

set -euo pipefail

ROOT_DIR="$(cd -- "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"

require_command() {
  if ! command -v "$1" >/dev/null 2>&1; then
    echo "Missing required command: $1" >&2
    exit 1
  fi
}

detect_firebase_project() {
  local config_file=""

  if [[ -f "$ROOT_DIR/.firebaserc" ]]; then
    config_file="$ROOT_DIR/.firebaserc"
  elif [[ -f "$ROOT_DIR/.firebaserc.example" ]]; then
    config_file="$ROOT_DIR/.firebaserc.example"
  fi

  if [[ -n "$config_file" ]]; then
    python3 - "$config_file" <<'PY'
import json
import sys

with open(sys.argv[1], "r", encoding="utf-8") as handle:
    data = json.load(handle)

projects = data.get("projects", {})
print(projects.get("default") or projects.get("staging") or "girisimkolay-local")
PY
    return
  fi

  echo "girisimkolay-local"
}

require_command firebase
require_command python3

FIREBASE_PROJECT="${FIREBASE_PROJECT_ID:-$(detect_firebase_project)}"

echo "[dev] Starting Firebase emulators for project: $FIREBASE_PROJECT"
cd "$ROOT_DIR"
firebase emulators:start --project "$FIREBASE_PROJECT" --only auth,firestore,functions,storage
