/*
 * Unless explicitly stated otherwise all files in this repository are licensed under the Apache License Version 2.0.
 * This product includes software developed at OpenObserve (https://www.openobserve.ai/).
 * Copyright 2016-Present OpenObserve, Inc.
 */
package com.openobserve.android.trace.internal

import com.openobserve.android.trace.api.span.OpenObserveSpan
import com.openobserve.android.trace.api.span.OpenObserveSpanBuilder
import com.openobserve.android.trace.api.span.OpenObserveSpanContext
import com.openobserve.android.trace.api.span.OpenObserveSpanLink
import com.openobserve.trace.bootstrap.instrumentation.api.AgentTracer

internal class OpenObserveSpanBuilderAdapter(
    private val delegate: AgentTracer.SpanBuilder,
    private val spanLogger: OpenObserveSpanLogger
) : OpenObserveSpanBuilder {

    override fun ignoreActiveSpan() = apply { delegate.ignoreActiveSpan() }

    override fun start(): OpenObserveSpan = OpenObserveSpanAdapter(delegate.start(), spanLogger)

    override fun withOrigin(origin: String?) = apply { delegate.withOrigin(origin) }

    override fun withStartTimestamp(micros: Long) = apply { delegate.withStartTimestamp(micros) }

    override fun withTag(key: String, value: Double?): OpenObserveSpanBuilder = apply { delegate.withTag(key, value) }

    override fun withTag(key: String, value: Long?): OpenObserveSpanBuilder = apply { delegate.withTag(key, value) }

    override fun withTag(key: String, value: Any?): OpenObserveSpanBuilder = apply { delegate.withTag(key, value) }

    override fun withLink(link: OpenObserveSpanLink): OpenObserveSpanBuilder = apply {
        delegate.withLink(OpenObserveSpanLinkAdapter(link))
    }

    override fun withResourceName(resourceName: String?): OpenObserveSpanBuilder = apply {
        delegate.withResourceName(resourceName)
    }

    override fun withParentContext(parentContext: OpenObserveSpanContext?): OpenObserveSpanBuilder = apply {
        if (parentContext is OpenObserveSpanContextAdapter) delegate.asChildOf(parentContext.delegate)
    }

    override fun withParentSpan(parentSpan: OpenObserveSpan?) = withParentContext(parentSpan?.context())
}
