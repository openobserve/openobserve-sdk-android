#!/usr/bin/env bash
#
# OpenObserve Android SDK — build & publish (manual + CI/CD).
#
# Publishes the ai.openobserve:o2-sdk-android-* modules to Maven Central via the
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
# Resolve the JDK before trusting PATH. `/usr/libexec/java_home` is NOT reliable here:
# on a stock macOS it reports only the Java 1.8 applet plugin and never sees a Homebrew
# JDK, so `java_home -v 17` silently hands back Java 8.
if [ -z "${JAVA_HOME:-}" ] || [ ! -x "${JAVA_HOME}/bin/java" ]; then
  for c in /opt/homebrew/opt/openjdk@17/libexec/openjdk.jdk/Contents/Home \
           /usr/local/opt/openjdk@17/libexec/openjdk.jdk/Contents/Home \
           /Library/Java/JavaVirtualMachines/openjdk-17.jdk/Contents/Home; do
    if [ -x "$c/bin/java" ]; then export JAVA_HOME="$c"; break; fi
  done
fi
JAVA_BIN="${JAVA_HOME:+$JAVA_HOME/bin/}java"
command -v "$JAVA_BIN" > /dev/null 2>&1 || { echo "ERROR: no java found. Install openjdk@17." >&2; exit 2; }
# Java 8 reports version "1.8.0_x" (major parses as 1); Java 17 reports "17.0.x".
JAVA_MAJOR=$("$JAVA_BIN" -version 2>&1 | head -1 | sed -E 's/.*version "([0-9]+).*/\1/')
# Bound BOTH ends. The unit tests use a reflection helper (tools/unit
# ReflectUtils.setFieldValue -> RemoveFinalModifier) that strips the `final` modifier — a
# technique the JDK blocked after 17, so JDK 21 (which Android Studio bundles) fails those
# tests even though the code is correct. The old guard only rejected > 17, so an
# unset JAVA_HOME falling back to the Java 1.8 applet plugin parsed as major "1", passed
# `1 > 17` == false, and failed later with an unrelated error.
if [ "${JAVA_MAJOR:-0}" -gt 17 ] || [ "${JAVA_MAJOR:-0}" -lt 11 ]; then
  echo "ERROR: JDK '$JAVA_MAJOR' at ${JAVA_HOME:-<PATH>} — this build needs JDK 11-17 (17 recommended)." >&2
  echo "       > 17: the unit tests' final-stripping reflection helper is blocked (Android Studio ships 21)." >&2
  echo "       < 11: too old to compile this project." >&2
  echo "       Fix: brew install openjdk@17" >&2
  echo "            export JAVA_HOME=/opt/homebrew/opt/openjdk@17/libexec/openjdk.jdk/Contents/Home" >&2
  exit 2
fi

# The Android SDK. publish.sh previously only mentioned ANDROID_HOME in a comment and never
# checked it, so a shell without it (CI, cron, any non-login shell) got
# "SDK location not found" from Gradle after the gates had already run.
if [ -z "${ANDROID_HOME:-}" ] && [ ! -f local.properties ]; then
  for c in "$HOME/Library/Android/sdk" /usr/local/share/android-sdk /opt/homebrew/share/android-sdk; do
    if [ -d "$c" ]; then export ANDROID_HOME="$c"; break; fi
  done
fi
if [ -z "${ANDROID_HOME:-}" ] && [ ! -f local.properties ]; then
  echo "ERROR: Android SDK not found. Set ANDROID_HOME or add sdk.dir=... to local.properties." >&2
  exit 2
fi
echo "    JDK $JAVA_MAJOR (${JAVA_HOME:-PATH}) | ANDROID_HOME ${ANDROID_HOME:-<local.properties>}"

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
  FULL_VERSION="$VERSION-$TYPE${NUMBER}"
  echo "OK. Published ai.openobserve:o2-sdk-android-* $FULL_VERSION to Maven Central (sync ~15-30 min)."
  cat <<EOF

────────────────────────────────────────────────────────────────────────────
MANUAL STEPS — this script does NOT do these. See RELEASING.md.
────────────────────────────────────────────────────────────────────────────

1. VERIFY on Maven Central (not here — this script only reports what Gradle did).
   Central lags 15-30 min. Both .pom AND .aar must return 200; a .pom alone is not
   a usable artifact. Pace the requests — unthrottled loops get rate-limited to 000
   and read as "missing":

     B=https://repo1.maven.org/maven2/ai/openobserve
     for m in core internal rum logs trace webview ndk flags session-replay; do
       for e in pom aar; do
         curl -s -o /dev/null -w "%{http_code} o2-sdk-android-\$m.\$e\n" \\
           "\$B/o2-sdk-android-\$m/$FULL_VERSION/o2-sdk-android-\$m-$FULL_VERSION.\$e"
       done; sleep 0.3
     done

   mvnrepository.com is a third-party crawler and lags by up to a day. Absence there
   is not evidence of a failed publish; repo1.maven.org is authoritative.

2. COMMIT + PUSH this branch. The published artifacts should have a matching commit
   on origin. This script tags nothing on Android.

3. RELEASE THE OTHER PLATFORMS in order — iOS may run in parallel, RN goes LAST and
   only once every native pin actually resolves:
     openobserve-sdk-ios/scripts/openobserve/publish.sh --version <x.y.z-alpha.N> --publish
     openobserve-react-native-rum/RELEASING.md      (manual — no script exists)

4. AFTER ANY UPSTREAM SYNC, regenerate what patches cannot carry:
     ./gradlew apiDump        # api/*.api + api/apiSurface + compiler-meta.txt

EOF
fi
