# patches/

Applied by `sync-upstream.sh` AFTER the rebrand codemod, in filename order.

The codemod (`rename-map.json`) reproduces every *string* rename. These patches carry the
changes it **cannot** express — so a fresh upstream sync still reproduces the fork exactly.

## 0001-post-rebrand-fixups.patch (14 files)
Fixes found by the publish dry-run. Each is something no string rule can do:

- **build.gradle.kts** — `packageGroup` (staging-profile auto-discovery; the plugin defaults
  to the root `group`, which this build never sets) + Datadog's `stagingProfileId` removed +
  the external `dependency-license` plugin disabled.
- **sample/{automotive,benchmark,kotlin,tv}** — disable the unpublished
  openobserve-sdk-android-gradle-plugin (#7) and its `datadog {}` extension usage.
- **flags schema `dd` -> `oo`** + **telemetry propagator enum `datadog` -> `openobserve`** —
  these regenerate the models, keeping them consistent with the renamed callers.
- **OpenObserveSiteExtensions** `tld "com" -> "ai"` — the host is assembled from PARTS, so
  the `datadoghq.com -> openobserve.ai` rule can't reach the tld.
- **bare-receiver `dd` -> `oo`** (LateCrashReporter, CoreTracerSpanToSpanEventMapper,
  WebViewRumEventMapper) — the `\.dd` rule needs a leading dot.
- **`ooSpanContext` -> `openObserveSpanContext`** (OkHttpExt, OtelTracerBuilderProviderTest) —
  the getter renamed via Datadog->OpenObserve, the usages via datadog->oo. Two rules, one name.
- **`"dd.configuration"` -> `"oo.configuration"`** (DeserializedViewEventAssert) — a field-path
  STRING literal; missing it silently un-ignored a field and looked like flaky tests.

## After every sync — regenerate, don't patch
Generated files are intentionally NOT in the patch:
```bash
./gradlew apiDump   # api/*.api + api/apiSurface, then commit
```
`verification.properties` (Gradle module metadata) likewise regenerates.

## Regenerating this patch
After changing the fork, re-derive it against a simulated sync:
1. worktree at `$(cat ../UPSTREAM_BASE)`, overlay `scripts/openobserve` + keep-ours, run
   `rebrand.mjs` to a fixpoint;
2. `git diff -R openobserve -- . ':!scripts/openobserve' ':!**/api/**' ':!**/verification.properties' ':!buildSrc/**/AndroidConfig.kt' ':!RELEASING.md' ':!.idea/**' ':!CHANGELOG.md'`
   (`-R` matters: the patch must transform synced -> desired);
3. verify `git apply --check` + zero drift.
