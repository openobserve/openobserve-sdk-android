/*
 * Unless explicitly stated otherwise all files in this repository are licensed under the Apache License Version 2.0.
 * This product includes software developed at Datadog (https://www.datadoghq.com/).
 * Copyright 2016-Present Datadog, Inc.
 */

package com.openobserve.android.core.internal.time

/**
 * NTP endpoints used to synchronize the SDK clock.
 *
 * openobserve: upstream pointed these at its own registered *vendor zone* in the NTP Pool Project
 * (`[0-3].<upstream-vendor>.pool.ntp.org`), and CoreFeature registers all of them by DEFAULT — so
 * every app embedding this SDK emitted DNS lookups naming the upstream vendor on startup. No SDK
 * data travels over NTP (UDP/123 carries timestamps only) and that zone resolves to community pool
 * servers rather than upstream infrastructure, but the lookups are visible to enterprise DNS
 * logging, proxies and packet capture, and are enough for SDK-fingerprinting tools to report an
 * OpenObserve app as embedding a competitor's SDK. It also made our clock sync depend on a DNS zone
 * a competitor controls and could repoint at will.
 *
 * The rebrand codemod could not reach these string literals: every upstream-identity rule requires
 * an uppercase letter or a suffix, and a bare lowercase hostname label matches none of them. See
 * scripts/openobserve/rename-map.json (intentionallyNotRebranded) and check-no-datadog.sh, which
 * now fails on any upstream-named network host.
 *
 * INTERIM: neutral public anycast NTP — no registration, globally distributed. These are the SAME
 * two providers the iOS SDK uses (ServerOffsetPublisher.OpenObserveNTPServers), so both platforms
 * behave identically. Note the NTP Pool Project explicitly asks vendors NOT to hardcode the generic
 * `pool.ntp.org` in distributed software — which is exactly why upstream registered a vendor zone —
 * so the generic pool is not a correct destination for us either.
 * TODO(openobserve): register our own `openobserve.pool.ntp.org` vendor zone at
 * https://www.ntppool.org/vendors.html and point these there — the proper long-term fix.
 *
 * Two providers rather than four hosts: the callers pick among the registered endpoints, and a
 * single entry would let one provider's outage stop clock sync entirely. On failure the offset
 * falls back to the device clock — degraded event ordering, never a crash.
 */
internal enum class OpenObserveNtpEndpoint(val host: String) {

    /**
     * Endpoint for the Network Time Protocol time syncing.
     */
    NTP_0("time.cloudflare.com"),

    /**
     * Endpoint for the Network Time Protocol time syncing.
     */
    NTP_1("time.google.com")
}
