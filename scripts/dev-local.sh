#!/usr/bin/env bash

set -euo pipefail

ROOT_DIR="$(cd -- "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
BACKEND_DIR="$ROOT_DIR/backend"

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

require_command python3
require_command firebase

if [[ ! -f "$BACKEND_DIR/.env" ]]; then
  echo "Missing backend/.env. Copy backend/.env.example to backend/.env and fill the required values." >&2
  exit 1
fi

FIREBASE_PROJECT="${FIREBASE_PROJECT_ID:-$(detect_firebase_project)}"
BACKEND_PID=""

cleanup() {
  if [[ -n "$BACKEND_PID" ]] && kill -0 "$BACKEND_PID" >/dev/null 2>&1; then
    kill "$BACKEND_PID" >/dev/null 2>&1 || true
    wait "$BACKEND_PID" 2>/dev/null || true
  fi
}

trap cleanup EXIT INT TERM

echo "[dev] Starting FastAPI backend on http://127.0.0.1:8000"
(
  cd "$BACKEND_DIR"
  python3 -m uvicorn app.main:app --reload --host 0.0.0.0 --port 8000
) &
BACKEND_PID="$!"

echo "[dev] Starting Firebase emulators for project: $FIREBASE_PROJECT"
cd "$ROOT_DIR"
firebase emulators:start --project "$FIREBASE_PROJECT" --only auth,firestore
