#!/usr/bin/env bash
#
# OpenObserve Android SDK — build & publish (manual + CI/CD).
#
# Publishes the ai.openobserve:oo-sdk-android-* modules to Maven Central via the
# Sonatype staging API. Safe by default: runs to Maven Local (a dry run) unless
# you pass --publish. The exact same script is used by CI — everything is driven
# by flags + environment variables, no interactive prompts.
#
# USAGE
#   scripts/openobserve/publish.sh --version 0.1.0 --type alpha --number 1            # dry run -> ~/.m2
#   scripts/openobserve/publish.sh --version 0.1.0 --type alpha --number 1 --publish  # -> Maven Central
#   scripts/openobserve/publish.sh --version 0.1.0 --type release --publish
#
#   --type  alpha|beta|rc|release   (default: alpha)
#   --number N                       alpha/beta/rc iteration (default: 1; ignored for release)
#   --publish                        actually push to Sonatype (omit = dry run to Maven Local)
#   --skip-tests                     skip unit tests (NOT for a real release)
#
# REQUIRED ENV (only when --publish):
#   CENTRAL_PUBLISHER_USERNAME  CENTRAL_PUBLISHER_PASSWORD   # Sonatype Central token
#   GPG_PRIVATE_KEY  GPG_PASSWORD                            # signing key (ASCII-armored)
#
# REQUIRED TOOLCHAIN: JDK 17+, Android SDK (ANDROID_HOME), a valid NDK for the -ndk module.
set -euo pipefail
cd "$(git rev-parse --show-toplevel)"   # repo root

VERSION="" ; TYPE="alpha" ; NUMBER="1" ; PUBLISH=0 ; SKIP_TESTS=0
while [ $# -gt 0 ]; do case "$1" in
  --version) VERSION="$2"; shift 2;;
  --type) TYPE="$2"; shift 2;;
  --number) NUMBER="$2"; shift 2;;
  --publish) PUBLISH=1; shift;;
  --skip-tests) SKIP_TESTS=1; shift;;
  *) echo "unknown arg: $1" >&2; exit 2;;
esac; done
[ -n "$VERSION" ] || { echo "ERROR: --version X.Y.Z required" >&2; exit 2; }

echo "==> OpenObserve Android publish: $VERSION-$TYPE${NUMBER} (publish=$PUBLISH)"

# --- 0. toolchain guard -------------------------------------------------------
# The unit tests use a reflection helper (tools/unit ReflectUtils.setFieldValue ->
# RemoveFinalModifier) that strips the `final` modifier — a technique the JDK blocked
# after 17. On JDK 21+ those tests fail with UnsupportedOperationException even though
# the code is correct. The project targets Java 11/17, so require <= 17.
JAVA_BIN="${JAVA_HOME:+$JAVA_HOME/bin/}java"
JAVA_MAJOR=$("$JAVA_BIN" -version 2>&1 | head -1 | sed -E 's/.*version "([0-9]+).*/\1/')
if [ "${JAVA_MAJOR:-0}" -gt 17 ] && [ "$SKIP_TESTS" = "0" ]; then
  echo "ERROR: JDK $JAVA_MAJOR detected; this build's unit tests require JDK 17 or lower." >&2
  echo "       (Android Studio bundles JDK 21 — do NOT use it here.)" >&2
  echo "       Fix: brew install openjdk@17 && export JAVA_HOME=\$(brew --prefix openjdk@17)/libexec/openjdk.jdk/Contents/Home" >&2
  echo "       Or re-run with --skip-tests to bypass (NOT for a real release)." >&2
  exit 2
fi
echo "    JDK $JAVA_MAJOR"

# --- 1. set the version in AndroidConfig.kt (single source of truth) -----------
CFG="buildSrc/src/main/kotlin/com/openobserve/gradle/config/AndroidConfig.kt"
IFS='.' read -r MAJ MIN PAT <<< "$VERSION"
case "$TYPE" in
  alpha)   VTYPE="Version.Type.Alpha($NUMBER)";;
  beta)    VTYPE="Version.Type.Beta($NUMBER)";;
  rc)      VTYPE="Version.Type.ReleaseCandidate($NUMBER)";;
  release) VTYPE="Version.Type.Release";;
  *) echo "ERROR: --type must be alpha|beta|rc|release" >&2; exit 2;;
esac
perl -0pi -e "s/val VERSION = Version\([^)]*\)[^\n]*/val VERSION = Version($MAJ, $MIN, $PAT, $VTYPE)/" "$CFG"
echo "    set $CFG -> Version($MAJ, $MIN, $PAT, $VTYPE)"

# --- 2. release gates ---------------------------------------------------------
echo "==> gate: zero-Datadog (shipped module source)"
scripts/openobserve/check-no-datadog.sh || { echo "FAILED zero-Datadog gate" >&2; exit 1; }
echo "==> gate: Apache-2.0 attribution retained"
[ -f scripts/openobserve/check-attribution.mjs ] && node scripts/openobserve/check-attribution.mjs || echo "    (attribution gate not present — skipping)"
# HARD GUARD: never publish under Datadog's Sonatype staging profile. Replace the
# stagingProfileId in build.gradle.kts with OpenObserve's (from the Central Portal
# after registering the ai.openobserve namespace). See RELEASING.md checklist.
if [ "$PUBLISH" = "1" ] && grep -q '378eecbbe2cf9' build.gradle.kts; then
  echo "FAILED: build.gradle.kts still has Datadog's stagingProfileId (378eecbbe2cf9)." >&2
  echo "        Replace it with OpenObserve's profile id before publishing (RELEASING.md)." >&2
  exit 1
fi

# --- 3. build + test ----------------------------------------------------------
echo "==> assembleLibrariesRelease"
./gradlew assembleLibrariesRelease --stacktrace
echo "==> apiCheck (published API surface must match committed .api)"
./gradlew checkApiSurfaceChangesAll || { echo "API surface stale — run ./gradlew apiDump and commit" >&2; exit 1; }
if [ "$SKIP_TESTS" = "0" ]; then
  echo "==> unitTestRelease"
  ./gradlew unitTestRelease --stacktrace
fi

# --- 4. publish ---------------------------------------------------------------
if [ "$PUBLISH" = "0" ]; then
  echo "==> DRY RUN: publishToMavenLocal (~/.m2/repository/ai/openobserve)"
  ./gradlew publishToMavenLocal -Poo-skip-signing
  echo "OK (dry run). Inspect ~/.m2/repository/ai/openobserve, then re-run with --publish."
else
  : "${CENTRAL_PUBLISHER_USERNAME:?set Sonatype token}" "${CENTRAL_PUBLISHER_PASSWORD:?}"
  : "${GPG_PRIVATE_KEY:?set signing key}" "${GPG_PASSWORD:?}"
  echo "==> publishToSonatype + close & release staging repo"
  ./gradlew publishToSonatype closeAndReleaseSonatypeStagingRepository --stacktrace
  echo "OK. Published ai.openobserve:oo-sdk-android-* $VERSION-$TYPE${NUMBER} to Maven Central (sync ~15-30 min)."
fi
