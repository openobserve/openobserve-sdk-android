#!/usr/bin/env bash
#
# Zero-Datadog gate (doc 08 §5). Fails if any SHIPPED module source carries a
# Datadog IDENTITY token (package, class, coordinate, native-lib, wire prefix).
# Intentionally excludes: the rebrand tooling (contains the token map), build
# output, and Apache-2.0 §4 attribution (per-file "developed at Datadog" license
# headers + NOTICE, which are legally retained — verified by check-attribution).
#
# Used by publish.sh and by CI. Exit 0 = clean.
set -euo pipefail
cd "$(git rev-parse --show-toplevel)"

# Search shipped library source only (not samples/tests/tooling/build).
PATHS=$(git ls-files 'features/*/src/main/*' 'oo-sdk-android-*/src/main/*' 'integrations/*/src/main/*' 2>/dev/null || true)
[ -n "$PATHS" ] || { echo "no shipped source found?"; exit 1; }

# Identity tokens that must never appear in shipped source. Wire prefixes
# (ddtags/ddsource/DD-API-KEY) are covered by step 2 and included here.
HITS=$(echo "$PATHS" | xargs grep -nE \
  'com\.datadog|com/datadog|com\.datadoghq|\bDatadog[A-Za-z]|\bDATADOG|datad0g|ddog-gov|\bddtags\b|\bddsource\b|DD-API-KEY|dogstatsd' \
  2>/dev/null \
  | grep -viE 'developed at Datadog|Copyright .*Datadog|licensed under' \
  || true)

if [ -n "$HITS" ]; then
  echo "ZERO-DATADOG GATE FAILED — Datadog identity token in shipped source:"
  echo "$HITS" | head -40
  echo "..."
  echo "total: $(echo "$HITS" | wc -l | tr -d ' ') lines"
  exit 1
fi
echo "zero-Datadog gate: PASS (shipped module source is clean)"
