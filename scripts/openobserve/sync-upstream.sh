#!/usr/bin/env bash
#
# OpenObserve openobserve-sdk-android upstream sync.
#
# Rebuilds the `openobserve` branch from a pristine upstream (DataDog/dd-sdk-android)
# commit: checkout upstream → keep-ours overlay → rebrand codemod (git mv package +
# module dirs, then content rewrite) → re-apply patches/ → record base.
#
# Usage: scripts/openobserve/sync-upstream.sh [<commit-ish>]
#   UPSTREAM_REMOTE (default: upstream), BASE_BRANCH (default: openobserve)
#
# Exit: 0 ok/up-to-date, 1 usage/precondition, 2 patch series failed to apply.
#
# NOTE: always follow a sync with a full Gradle + NDK build — the rebrand is
# mechanical but a missed path/JNI symbol only surfaces at compile/link time.
set -euo pipefail

UPSTREAM_REMOTE=${UPSTREAM_REMOTE:-upstream}
BASE_BRANCH=${BASE_BRANCH:-openobserve}
REPO_ROOT=$(git rev-parse --show-toplevel)
cd "$REPO_ROOT"

if [ -n "$(git status --porcelain -uno)" ]; then
  echo "error: working tree has uncommitted changes" >&2
  exit 1
fi

git fetch "$UPSTREAM_REMOTE" --prune --tags --quiet

TOOLING=$(mktemp -d); trap 'rm -rf "$TOOLING"' EXIT
git show "$BASE_BRANCH:scripts/openobserve/rebrand.mjs"     > "$TOOLING/rebrand.mjs"
git show "$BASE_BRANCH:scripts/openobserve/rename-map.json" > "$TOOLING/rename-map.json"
git show "$BASE_BRANCH:scripts/openobserve/keep-ours.txt"   > "$TOOLING/keep-ours.txt"
CURRENT_BASE=$(git show "$BASE_BRANCH:scripts/openobserve/UPSTREAM_BASE" 2>/dev/null | tr -d '[:space:]' || echo none)

TARGET_REF=${1:-"$UPSTREAM_REMOTE/develop"}
TARGET=$(git rev-parse --verify "${TARGET_REF}^{commit}")
SHORT=$(git rev-parse --short "$TARGET")
if [ "$TARGET" = "$CURRENT_BASE" ]; then echo "Already up to date with $SHORT."; exit 0; fi

echo "Syncing upstream ${CURRENT_BASE:0:12} -> $TARGET"
SYNC_BRANCH="sync/upstream-$SHORT"
git checkout -B "$SYNC_BRANCH" "$BASE_BRANCH"
git rm -rfq . >/dev/null
git checkout "$TARGET" -- .
git clean -fd --quiet

grep -vE '^\s*(#|$)' "$TOOLING/keep-ours.txt" | while read -r p; do
  if git cat-file -e "$BASE_BRANCH:$p" 2>/dev/null; then rm -rf "$p"; git checkout "$BASE_BRANCH" -- "$p"; fi
done

# scripts/openobserve was restored by keep-ours, but run from the temp copy so the
# rename-map/rebrand survive the tree replacement.
cp "$TOOLING/rename-map.json" "$TOOLING/keep-ours.txt" scripts/openobserve/ 2>/dev/null || true
node "$TOOLING/rebrand.mjs"

if compgen -G "scripts/openobserve/patches/*.patch" > /dev/null; then
  for p in scripts/openobserve/patches/*.patch; do
    echo "applying $p"
    git apply --3way "$p" || { echo "error: patch $p failed" >&2; exit 2; }
  done
fi

echo "$TARGET" > scripts/openobserve/UPSTREAM_BASE
git add -A
git commit --quiet -m "chore(sync): rebrand upstream dd-sdk-android @$SHORT as OpenObserve"
echo "Sync branch $SYNC_BRANCH ready. Review, run a full Gradle+NDK build, push, open a PR against $BASE_BRANCH."
