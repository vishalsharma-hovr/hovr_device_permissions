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

ACTIVE_USER="$(gh api user --jq .login 2>/dev/null || true)"
if [[ -n "$ACTIVE_USER" && "$ACTIVE_USER" != "$GITHUB_OWNER" ]]; then
  if gh auth switch -u "$GITHUB_OWNER" >/dev/null 2>&1; then
    echo "Switched gh active account to $GITHUB_OWNER"
  else
    echo "Active gh account is $ACTIVE_USER but publishing to $GITHUB_OWNER."
    echo "Run: gh auth switch -u $GITHUB_OWNER"
    exit 1
  fi
fi
gh auth setup-git -h github.com >/dev/null 2>&1 || true

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

rsync -a --delete \
  --exclude '.git' \
  --exclude 'android/**/build/' \
  --exclude 'android/local.properties' \
  --exclude 'android/.gradle/' \
  --exclude '.gradle/' \
  --exclude 'build/' \
  --exclude '.build/' \
  --exclude '.swiftpm/' \
  "$ROOT/" ./

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
echo ""
echo "Next steps:"
echo "  1. JitPack: https://jitpack.io/#${GITHUB_OWNER}/${REPO_NAME}/${VERSION}"
echo "  2. Wait for green Android build, then bump host apps:"
echo "     implementation 'com.github.${GITHUB_OWNER}:${REPO_NAME}:${VERSION}'"
echo "     pod 'HovrDevicePermissions', :git => '${REMOTE_URL}', :tag => '${VERSION}'"
echo "  3. Revert any local project(':${REPO_NAME}') / :path pod wiring in rider/driver."
echo "  4. Update doc/INTEGRATION.md current release table."
