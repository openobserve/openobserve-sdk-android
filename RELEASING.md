# Releasing the OpenObserve mobile SDKs

Publish order is **bottom-up**: native cores first (Android + iOS, independent), then
React Native. Each `publish.sh` is dry-run by default (add `--publish` to go live) and
runs the release gates automatically. The **same script runs in CI** — nothing here is
interactive.

---

## ⛔ MUST-DO before the first publish (do not skip)

- [ ] **Register the Sonatype namespace `ai.openobserve`** at <https://central.sonatype.com>
      (reverse-DNS of openobserve.ai). Verify via the DNS TXT record it gives you.
      *Alternative:* `io.github.openobserve` verified via a marker repo in the
      github.com/openobserve org — if you switch, change the Maven group id in all forks.
- [x] ~~Replace the Sonatype staging profile id~~ — **DONE**: the hardcoded id is removed; the
      nexus-publish plugin now **auto-discovers** the staging profile for `ai.openobserve`
      from your account. (The Central Portal "View ID" UUID is a *namespace* id, NOT an
      OSSRH staging profile id — it must not be pinned here.) The publish guard stays in
      place to block Datadog's id ever returning.
- [ ] **Credentials** (store as CI secrets, not on a laptop):
      - Android: `CENTRAL_PUBLISHER_USERNAME`, `CENTRAL_PUBLISHER_PASSWORD` (Central user token)
        + `GPG_PRIVATE_KEY` (ASCII-armored), `GPG_PASSWORD` (Maven Central requires signing;
        publish the public key to `keys.openpgp.org`).
      - iOS: `pod trunk register you@openobserve.ai 'OpenObserve'` (email-verified) →
        stores a trunk token; or `COCOAPODS_TRUNK_TOKEN`.
      - RN: publish under the `@openobserve` npm org → `npm login` / `NPM_TOKEN`.
- [ ] **Team ownership** (so releases aren't tied to one person): add members to the
      Sonatype namespace, `pod trunk add-owner <Pod> <email>` per pod, and the npm org team.

---

## Release order & commands

### 1. Android → Maven Central  (`ai.openobserve:oo-sdk-android-*`)
```bash
cd openobserve-sdk-android
./gradlew apiDump                                  # refresh the 28 .api ABI files after the rebrand renames — commit the result
scripts/openobserve/publish.sh --version 0.1.0 --type alpha --number 1            # DRY RUN -> ~/.m2, inspect
scripts/openobserve/publish.sh --version 0.1.0 --type alpha --number 1 --publish  # -> Maven Central (needs CENTRAL_* + GPG_*)
```
Toolchain: JDK 17+, Android SDK, a valid NDK (`sdkmanager --install "ndk;28.0.13004108"`).
Maven resolves transitively, so module order doesn't matter.

### 2. iOS → CocoaPods  (in parallel with Android)
```bash
cd openobserve-sdk-ios
DEVELOPER_DIR=/Applications/Xcode.app/Contents/Developer \
  scripts/openobserve/publish.sh --version 0.1.0-alpha.1            # DRY RUN: lint every pod
DEVELOPER_DIR=/Applications/Xcode.app/Contents/Developer \
  scripts/openobserve/publish.sh --version 0.1.0-alpha.1 --publish  # -> trunk (needs COCOAPODS_TRUNK_TOKEN)
```
Toolchain: full **Xcode** (`sudo xcodebuild -license accept`; install the iOS platform
via Xcode ▸ Settings ▸ Components or `xcodebuild -downloadPlatform iOS`), CocoaPods.
The script pushes pods **in dependency order — `OpenObserveInternal` first**.

### 3. React Native → npm  (only after 1 AND 2 publish)
```bash
cd openobserve-react-native-rum
# flip the deferredRules so the native deps point at the published cores + re-pin versions:
#   packages/core/android/build.gradle : com.datadoghq:dd-sdk-android-*:3.10.0 -> ai.openobserve:oo-sdk-android-*:0.1.0-alpha.1
#   packages/core/*.podspec            : DatadogCore/RUM 3.11.0 -> OpenObserveCore/RUM 0.1.0-alpha.1
yarn && yarn build
npm publish --workspaces --access public --tag alpha    # @openobserve/mobile-react-native first, then satellites
```

---

## Alpha → beta → stable
- Prove the pipeline with `--type alpha --number 1` (Android) / `-alpha.1` (iOS/npm `--tag alpha`).
- Bump `--number`/suffix for each iteration; `--type release` (Android) + drop the suffix for stable.
- Keep one shared version across all native modules so RN pins a single version.

## Gates every `publish.sh` enforces (fails the release on any)
- zero-Datadog (`check-no-datadog.sh`) · Apache-2.0 attribution (`check-attribution.mjs`)
- Android: `assembleLibrariesRelease` + `checkApiSurfaceChangesAll` + unit tests + the
  Datadog-profile-id guard.  iOS: `xcodebuild` (iOS) + `pod lib lint` per pod.
