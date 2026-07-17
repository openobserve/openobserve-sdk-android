/*
 * Unless explicitly stated otherwise all files in this repository are licensed under the Apache License Version 2.0.
 * This product includes software developed at Datadog (https://www.datadoghq.com/).
 * Copyright 2016-Present Datadog, Inc.
 */

package com.openobserve.android.core.internal.net

import com.openobserve.android.lint.InternalApi
import com.openobserve.android.trace.TracingHeaderType
import okhttp3.HttpUrl
import okhttp3.HttpUrl.Companion.toHttpUrlOrNull
import java.util.Locale

/**
 * Default implementation of [FirstPartyHostHeaderTypeResolver].
 *
 * @param hosts [Map] of hosts and associated tracing header types to initialize instance with.
 */
@InternalApi
class DefaultFirstPartyHostHeaderTypeResolver(
    hosts: Map<String, Set<TracingHeaderType>>
) : FirstPartyHostHeaderTypeResolver {

    internal var knownHosts = hosts.entries.associate { it.key.lowercase(Locale.US) to it.value }
        private set

    /** @inheritdoc */
    override fun isFirstPartyUrl(url: HttpUrl): Boolean {
        val host = url.host
        return knownHosts.keys.any {
            it == "*" || host == it || host.endsWith(".$it")
        }
    }

    /** @inheritdoc */
    override fun isFirstPartyUrl(url: String): Boolean {
        val httpUrl = url.toHttpUrlOrNull() ?: return false
        return isFirstPartyUrl(httpUrl)
    }

    /** @inheritdoc */
    override fun headerTypesForUrl(url: String): Set<TracingHeaderType> {
        val httpUrl = url.toHttpUrlOrNull() ?: return emptySet()
        return headerTypesForUrl(httpUrl)
    }

    /** @inheritdoc */
    override fun headerTypesForUrl(url: HttpUrl): Set<TracingHeaderType> {
        val host = url.host

        return knownHosts[host]
            ?: knownHosts.entries.firstOrNull { host.endsWith(".${it.key}") }?.value
            ?: knownHosts["*"]
            ?: emptySet()
    }

    /** @inheritdoc */
    override fun getAllHeaderTypes(): Set<TracingHeaderType> {
        return knownHosts.values.flatten().toSet()
    }

    /** @inheritdoc */
    override fun isEmpty(): Boolean {
        return knownHosts.isEmpty()
    }

    internal fun addKnownHosts(hosts: List<String>) {
        // openobserve: upstream defaulted to OPENOBSERVE (its proprietary `x-<vendor>-*` format)
        // + TRACECONTEXT here, so simply naming a first-party host injected a competitor's headers
        // into its requests with no opt-in. Those headers are only read by an upstream-instrumented
        // APM; OpenObserve's backend speaks W3C `traceparent`. Default to the interoperable format
        // we actually read — callers wanting another type use addKnownHostsWithHeaderTypes().
        knownHosts = knownHosts + hosts.associate {
            it.lowercase(Locale.US) to setOf(TracingHeaderType.TRACECONTEXT)
        }
    }

    internal fun addKnownHostsWithHeaderTypes(
        hostsWithHeaderTypes: Map<String, Set<TracingHeaderType>>
    ) {
        knownHosts = knownHosts + hostsWithHeaderTypes.entries.associate {
            it.key.lowercase(Locale.US) to it.value
        }
    }
}
