/*
 * Unless explicitly stated otherwise all files in this repository are licensed under the Apache License Version 2.0.
 * This product includes software developed at OpenObserve (https://www.openobserve.ai/).
 * Copyright 2016-Present OpenObserve, Inc.
 */
package com.openobserve.android.okhttp.trace

import com.openobserve.android.core.sampling.Sampler
import com.openobserve.android.trace.api.propagation.OpenObservePropagation
import com.openobserve.android.trace.api.span.OpenObserveSpan
import com.openobserve.android.trace.api.span.OpenObserveSpanBuilder
import com.openobserve.android.trace.api.span.OpenObserveSpanContext
import com.openobserve.android.trace.api.trace.OpenObserveTraceId
import com.openobserve.android.trace.api.tracer.OpenObserveTracer
import com.openobserve.android.trace.internal.fromHex
import fr.xgouchet.elmyr.Forge
import okhttp3.Request
import org.mockito.kotlin.any
import org.mockito.kotlin.anyOrNull
import org.mockito.kotlin.doAnswer
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.doThrow
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever

internal fun newAgentPropagationMock(
    extractedContext: OpenObserveSpanContext = mock()
) = mock<OpenObservePropagation> {
    on { extract(any<Request>(), any()) } doReturn extractedContext
}
internal fun OpenObservePropagation.wheneverInjectThenThrow(throwable: Throwable) {
    doThrow(throwable)
        .whenever(this)
        .inject(any<OpenObserveSpanContext>(), any<Request.Builder>(), any())
}

internal fun OpenObservePropagation.wheneverInjectThenValueToHeaders(key: String, value: String) {
    doAnswer { invocation ->
        val carrier = invocation.getArgument<Request.Builder>(1)
        val setter = invocation.getArgument<(carrier: Request.Builder, key: String, value: String) -> Unit>(2)
        setter.invoke(carrier, key, value)
    }
        .whenever(this)
        .inject(any<OpenObserveSpanContext>(), any<Request.Builder>(), any())
}

internal fun OpenObservePropagation.wheneverInjectCalledPassContextToHeaders(
    ooContext: Map<String, String>,
    nonOpenObserveContextKey: String,
    nonOpenObserveContextKeyValue: String
) {
    doAnswer { invocation ->
        val carrier = invocation.getArgument<Request.Builder>(1)
        val setter = invocation.getArgument<(carrier: Request.Builder, key: String, value: String) -> Unit>(2)
        ooContext.forEach { setter.invoke(carrier, it.key, it.value) }
        setter.invoke(carrier, nonOpenObserveContextKey, nonOpenObserveContextKeyValue)
    }
        .whenever(this)
        .inject(any<OpenObserveSpanContext>(), any<Request.Builder>(), any())
}

internal fun Forge.aOpenObserveTraceId(
    fakeString: String? = null
) = OpenObserveTraceId.fromHex(fakeString ?: aStringMatching("[a-f0-9]{31}"))

internal fun Forge.newTraceSamplerMock(
    span: OpenObserveSpan = newSpanMock()
) = mock<Sampler<OpenObserveSpan>> {
    on { sample(span) } doReturn true
}

internal fun Forge.newTracerMock(
    spanBuilder: OpenObserveSpanBuilder = newSpanBuilderMock(),
    propagation: OpenObservePropagation = newAgentPropagationMock()
) = mock<OpenObserveTracer> {
    on { buildSpan(TracingInterceptor.SPAN_NAME) } doReturn spanBuilder
    on { propagate() } doReturn propagation
}

internal inline fun <reified T : OpenObserveSpanContext> Forge.newSpanContextMock(
    fakeTraceId: OpenObserveTraceId = aOpenObserveTraceId(),
    fakeSpanId: Long = aLong(),
    samplingPriority: Int = 0
): T = mock<T> {
    on { spanId } doReturn fakeSpanId
    on { traceId } doReturn fakeTraceId
    on { mock.samplingPriority } doReturn samplingPriority
}

internal fun Forge.newSpanMock(
    context: OpenObserveSpanContext = newSpanContextMock(),
    samplingPriority: Int? = null
) = mock<OpenObserveSpan> {
    on { context() } doReturn context
    on { this.samplingPriority } doReturn samplingPriority
}

internal fun Forge.newSpanBuilderMock(
    localSpan: OpenObserveSpan = newSpanMock(),
    context: OpenObserveSpanContext = newSpanContextMock()
) = mock<OpenObserveSpanBuilder> {
    on { withOrigin(anyOrNull()) } doReturn it
    on { withParentContext(context) } doReturn it
    on { withParentContext(null as OpenObserveSpanContext?) } doReturn it
    on { start() } doReturn localSpan
}
