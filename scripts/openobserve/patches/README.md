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

## 0002-alpha2-privacy-fixes.patch (6 files)
The alpha2 privacy fixes. Each is a behavioural change to a value, not a token rename, so no
`rename-map.json` rule can express it — and a sync WILL revert them (verified: a freshly synced
tree restores `0.<upstream-vendor>.pool.ntp.org`, and check-no-datadog.sh then fails on it).

- **OpenObserveNtpEndpoint + CoreFeature** — the SDK contacted the upstream vendor's NTP pool by
  DEFAULT on every init. Now two neutral anycast providers (the same pair iOS uses). CoreFeature
  enumerates the endpoints rather than naming NTP_0..NTP_3, so changing the set can't silently
  break the call site.
- **ApmNetworkInstrumentationConfiguration / DefaultFirstPartyHostHeaderTypeResolver /
  ConfigDefaults** — three defaults injected the upstream vendor's proprietary `x-<vendor>-*`
  headers into every first-party request with NO opt-in. All now W3C Trace Context only.
- **DefaultFirstPartyHostHeaderTypeResolverTest** — upstream's test ASSERTED that injection as
  expected behaviour, so a correct fix turned the suite red. Inverted into a guard.

## Generating these patches — two traps that silently lose fixes

**1. Do NOT exclude `**/api/**`.** It looks like it targets the generated API surface, but it also
matches real source packages named `api` — e.g.
`features/oo-sdk-android-trace-internal/src/main/java/com/openobserve/trace/api/ConfigDefaults.java`.
That glob silently dropped the propagator-default fix from this patch. Exclude the generated files
precisely instead:
```
':!**/api/apiSurface' ':!**/api/*.api' ':!**/api/compiler-meta.txt'
```
(60 generated files vs 223 real source files under an `api/` package.)

**2. Never write the upstream vendor's name in our own comments.** rebrand.mjs runs over the whole
tree, including our prose. Two comments in build.gradle.kts were rewritten from "Datadog's
stagingProfileId" to "OpenObserve's stagingProfileId" — inverting the meaning of the comment that
explains why that profile id must never be reused. Say "the upstream vendor". The mirror image of
this also bites: quoting a forbidden token in a comment trips check-no-datadog.sh.

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
