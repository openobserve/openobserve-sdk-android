/*
 * Unless explicitly stated otherwise all files in this repository are licensed under the Apache License Version 2.0.
 * This product includes software developed at Datadog (https://www.datadoghq.com/).
 * Copyright 2016-Present Datadog, Inc.
 */
package com.openobserve.android.trace.internal

import com.openobserve.android.api.feature.FeatureSdkCore
import com.openobserve.android.trace.api.propagation.OpenObservePropagation
import com.openobserve.android.trace.api.scope.OpenObserveScope
import com.openobserve.android.trace.api.scope.OpenObserveScopeListener
import com.openobserve.android.trace.api.span.OpenObserveSpan
import com.openobserve.android.trace.api.span.OpenObserveSpanBuilder
import com.openobserve.android.trace.api.tracer.OpenObserveTracer
import com.openobserve.android.trace.internal.RumContextPropagator.Companion.injectRumContext
import com.openobserve.trace.bootstrap.instrumentation.api.AgentTracer
import com.openobserve.trace.bootstrap.instrumentation.api.ScopeSource

internal class OpenObserveTracerAdapter(
    internal val sdkCore: FeatureSdkCore,
    internal val delegate: AgentTracer.TracerAPI,
    internal val bundleWithRumEnabled: Boolean,
    private val spanLogger: OpenObserveSpanLogger,
    private val rumContextPropagator: RumContextPropagator = RumContextPropagator { sdkCore }
) : OpenObserveTracer {

    override fun buildSpan(instrumentationName: String, spanName: CharSequence): OpenObserveSpanBuilder = wrapSpan(
        delegate.buildSpan(instrumentationName, spanName)
    )

    @Suppress("DEPRECATION")
    override fun buildSpan(spanName: CharSequence): OpenObserveSpanBuilder = wrapSpan(delegate.buildSpan(spanName))

    override fun addScopeListener(scopeListener: OpenObserveScopeListener) {
        delegate.addScopeListener(OpenObserveScopeListenerAdapter(scopeListener))
    }

    override fun propagate(): OpenObservePropagation = OpenObservePropagationAdapter(
        internalLogger = sdkCore.internalLogger,
        delegate = delegate.propagate()
    )

    override fun activeSpan(): OpenObserveSpan? = delegate.activeSpan()?.let { OpenObserveSpanAdapter(it, spanLogger) }

    override fun activateSpan(span: OpenObserveSpan): OpenObserveScope? = (span as? OpenObserveSpanAdapter)?.let {
        OpenObserveScopeAdapter(
            delegate.activateSpan(span.delegate, ScopeSource.INSTRUMENTATION) ?: return null
        )
    }

    internal fun activateSpan(span: OpenObserveSpan, asyncPropagating: Boolean): OpenObserveScope? {
        return (span as? OpenObserveSpanAdapter)
            ?.let { delegate.activateSpan(it.delegate, ScopeSource.INSTRUMENTATION, asyncPropagating) }
            ?.let { OpenObserveScopeAdapter(it) }
    }

    private fun wrapSpan(span: AgentTracer.SpanBuilder) =
        OpenObserveSpanBuilderAdapter(span, spanLogger)
            .withRumContextIfNeeded()

    private fun OpenObserveSpanBuilder.withRumContextIfNeeded() = apply {
        if (bundleWithRumEnabled) injectRumContext(rumContextPropagator)
    }
}
