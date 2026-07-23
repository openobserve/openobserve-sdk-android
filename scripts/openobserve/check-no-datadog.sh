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
PATHS=$(git ls-files 'features/*/src/main/*' 'o2-sdk-android-*/src/main/*' 'integrations/*/src/main/*' 2>/dev/null || true)
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

# NOTE: `dd=s:` / `dd=p:` catch the W3C TRACESTATE vendor key. Every other pattern here
# needs an uppercase letter or the `hq` suffix, so a bare lowercase `dd=` matched none —
# and it is on the DEFAULT path now that tracecontext is the default propagator. Do not remove.
# --- CONTENT ------------------------------------------------------------------
# Identity tokens that must never appear in shipped source. Wire prefixes
# (ddtags/ddsource/DD-API-KEY) are covered by step 2 and included here.
#
# NOTE: `\bdd=s:|\bdd=p:|datadog\.pool` / `datadog\.<label>\.<tld>` catch HOSTNAMES. Every other pattern here
# needs an uppercase letter or the `hq` suffix, so a bare lowercase hostname label matched NONE
# of them — and this gate reported PASS while the core module shipped
# `[0-3].datadog.pool.ntp.org`, contacted by DEFAULT on every SDK init via CoreFeature. That went
# out in the published 0.1.0-alpha1 (as oo-sdk-android-core, the coordinate at the time) and can
# only be superseded, never recalled. Do not remove.
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

# --- BUILD TOOLING (buildSrc) -------------------------------------------------
# buildSrc does not ship, so it is not part of the zero-Datadog *artifact* claim — but an
# identity token here is not merely cosmetic, it BREAKS THE BUILD. GenerateCompilerMetaTask
# selected a representative class file with `it.path.contains("datadog")`; once the rebrand
# renamed the package to com/openobserve/ that filter matched zero files and every
# `./gradlew assembleLibrariesDebug` failed with "Couldn't find any class file to get
# compilation metadata". It was invisible to the section above (which never scanned buildSrc)
# AND to every rename-map rule (bare lowercase `datadog`: no word boundary, no uppercase, no
# `hq` suffix — the same blind spot that shipped the NTP hosts and the dd= tracestate key).
#
# The bare quoted "datadog" pattern is deliberately included here and NOT above: shipped
# source legitimately has none, while build tooling is where such literals hide.
BUILDSRC=$(git ls-files 'buildSrc/**' 2>/dev/null || true)
if [ -n "$BUILDSRC" ]; then
  BS_HITS=$(echo "$BUILDSRC" | xargs grep -nE \
    'com\.datadog|com/datadog|com\.datadoghq|\bDatadog[A-Za-z]|\bDATADOG|datad0g|"datadog"' \
    2>/dev/null \
    | grep -viE 'developed at Datadog|Copyright .*Datadog|licensed under' \
    || true)
  if [ -n "$BS_HITS" ]; then
    echo "ZERO-DATADOG GATE FAILED — Datadog identity token in buildSrc (breaks the build, not just branding):"
    echo "$BS_HITS" | head -20
    echo "total: $(echo "$BS_HITS" | wc -l | tr -d ' ') lines"
    FAIL=1
  fi
fi

# NOT scanned on purpose: features/*/clone_dd_trace.gradle.kts. Its datadog/trace/** entries are
# paths inside the UPSTREAM dd-trace-java repository being cloned — external coordinates, like
# @datadog/datadog-ci in the RN fork. Renaming them makes the clone 404. It sits outside every
# glob above (not under src/main/, not under buildSrc/); keep it that way.

[ "$FAIL" = "0" ] || exit 1
echo "zero-Datadog gate: PASS (shipped module source + buildSrc clean — paths + content)"
