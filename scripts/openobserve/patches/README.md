# Functional patch series (openobserve-sdk-android)

Empty at scaffold time. The **one** functional change this fork needs beyond
rebranding is the OpenObserve-only intake in the request factories (doc 04 §5):

| Patch | Concern | State |
|-------|---------|-------|
| `01-openobserve-intake.patch` | Request factories default to OpenObserve `/rum`,`/logs` and **fail closed** if no custom endpoint (never a Datadog `site` fallback) | todo (author post-rebrand) |

Make patches **against the rebranded tree** (after `rebrand.mjs` runs), single-concern,
numeric-prefixed. `sync-upstream.sh` re-applies them 3-way after each upstream rebrand.

Create: `git diff -- <path> > scripts/openobserve/patches/NN-name.patch`.
