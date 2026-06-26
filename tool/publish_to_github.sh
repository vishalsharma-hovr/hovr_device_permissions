#!/usr/bin/env bash
# Publishes to https://github.com/vishalsharma-hovr/hovr_device_permissions

set -euo pipefail

ROOT="$(cd "$(dirname "$0")/.." && pwd)"
VERSION="${1:-}"
GITHUB_OWNER="${GITHUB_OWNER:-vishalsharma-hovr}"
REPO_NAME="${REPO_NAME:-hovr_device_permissions}"
REMOTE_URL="${REMOTE_URL:-https://github.com/${GITHUB_OWNER}/${REPO_NAME}.git}"
WORK_DIR="$(mktemp -d)"
RELEASE_BRANCH="release/${VERSION}"

if [[ -z "$VERSION" ]]; then
  echo "Usage: ./tool/publish_to_github.sh v1.0.0"
  exit 1
fi

if [[ ! "$VERSION" =~ ^v[0-9]+\.[0-9]+\.[0-9]+$ ]]; then
  echo "Version must look like v1.0.0 (got: $VERSION)"
  exit 1
fi

cleanup() { rm -rf "$WORK_DIR"; }
trap cleanup EXIT

if ! command -v gh >/dev/null 2>&1; then
  echo "gh CLI not found. Install with: brew install gh && gh auth login"
  exit 1
fi
if ! gh auth status >/dev/null 2>&1; then
  echo "GitHub CLI is not authenticated. Run: gh auth login"
  exit 1
fi

if gh repo view "${GITHUB_OWNER}/${REPO_NAME}" >/dev/null 2>&1; then
  git clone "$REMOTE_URL" "$WORK_DIR/repo"
  cd "$WORK_DIR/repo"
  git checkout -B "$RELEASE_BRANCH"
else
  mkdir -p "$WORK_DIR/repo"
  cd "$WORK_DIR/repo"
  git init -b main
  gh repo create "${GITHUB_OWNER}/${REPO_NAME}" --public --source=. --remote=origin
  git checkout -B "$RELEASE_BRANCH"
fi

rsync -a --delete --exclude '.git' "$ROOT/" ./

git add -A
if git diff --cached --quiet; then
  echo "No changes to publish."
  exit 0
fi
git commit -m "Release $VERSION"
git tag -f "$VERSION"
git push -u origin "$RELEASE_BRANCH" --force
git push origin "$VERSION" --force

echo "Published $VERSION to $REMOTE_URL"
