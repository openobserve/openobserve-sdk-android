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

FAIL=0

# --- PATHS --------------------------------------------------------------------
# A Datadog-named DIRECTORY ships inside the artifact even when every file's content
# is clean, so a content-only grep reports PASS on a dirty artifact. This is not
# hypothetical: it is exactly what happened in the iOS fork, where
# OpenObserveInternal/Sources/NetworkInstrumentation/Datadog and
# TestUtilities/Sources/Mocks/Datadog* sat inside two PUBLISHED pods while this gate's
# counterpart reported PASS. Android is currently clean here (package dirs follow the
# renamed `package` declarations, and res/assets carry no Datadog names in shipped
# modules) — this keeps it that way across upstream syncs.
PATH_HITS=$(echo "$PATHS" | grep -iE '(^|/)datadog|com/datadog' || true)
if [ -n "$PATH_HITS" ]; then
  echo "ZERO-DATADOG GATE FAILED — Datadog-named PATH in shipped module source:"
  echo "$PATH_HITS" | head -40
  echo "total: $(echo "$PATH_HITS" | wc -l | tr -d ' ')"
  FAIL=1
fi

# --- CONTENT ------------------------------------------------------------------
# Identity tokens that must never appear in shipped source. Wire prefixes
# (ddtags/ddsource/DD-API-KEY) are covered by step 2 and included here.
#
# NOTE: `datadog\.pool` / `datadog\.<label>\.<tld>` catch HOSTNAMES. Every other pattern here
# needs an uppercase letter or the `hq` suffix, so a bare lowercase hostname label matched NONE
# of them — and this gate reported PASS while oo-sdk-android-core shipped
# `[0-3].datadog.pool.ntp.org`, contacted by DEFAULT on every SDK init via CoreFeature. That went
# out in the published 0.1.0-alpha1 and can only be superseded, never recalled. Do not remove.
HITS=$(echo "$PATHS" | xargs grep -nE \
  'com\.datadog|com/datadog|com\.datadoghq|\bDatadog[A-Za-z]|\bDATADOG|datad0g|ddog-gov|\bddtags\b|\bddsource\b|DD-API-KEY|dogstatsd|datadog\.pool|datadog\.[a-z]+\.(org|com|net)' \
  2>/dev/null \
  | grep -viE 'developed at Datadog|Copyright .*Datadog|licensed under' \
  || true)

if [ -n "$HITS" ]; then
  echo "ZERO-DATADOG GATE FAILED — Datadog identity token in shipped source CONTENT:"
  echo "$HITS" | head -40
  echo "..."
  echo "total: $(echo "$HITS" | wc -l | tr -d ' ') lines"
  FAIL=1
fi

[ "$FAIL" = "0" ] || exit 1
echo "zero-Datadog gate: PASS (shipped module source is clean — paths + content)"
