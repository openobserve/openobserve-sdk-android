/*
 * Unless explicitly stated otherwise all files in this repository are licensed under the Apache License Version 2.0.
 * This product includes software developed at OpenObserve (https://www.openobserve.ai/).
 * Copyright 2016-Present OpenObserve, Inc.
 */
package com.openobserve.android.trace.internal.net

import com.openobserve.android.api.feature.Feature
import com.openobserve.android.api.feature.FeatureSdkCore
import com.openobserve.android.api.instrumentation.network.HttpRequestInfo
import com.openobserve.android.core.sampling.Sampler
import com.openobserve.android.trace.api.OpenObserveTracingConstants.PrioritySampling
import com.openobserve.android.trace.api.OpenObserveTracingConstants.Tags
import com.openobserve.android.trace.api.span.OpenObserveSpan
import com.openobserve.android.trace.api.tracer.OpenObserveTracer
import com.openobserve.android.trace.internal.ApmNetworkInstrumentation.Companion.AGENT_PSR_ATTRIBUTE
import com.openobserve.android.trace.internal.ApmNetworkInstrumentation.Companion.ALL_IN_SAMPLE_RATE
import com.openobserve.android.trace.internal.ApmNetworkInstrumentation.Companion.SPAN_NAME
import com.openobserve.android.trace.internal.ApmNetworkInstrumentation.Companion.URL_QUERY_PARAMS_BLOCK_SEPARATOR
import com.openobserve.android.trace.internal.ApmNetworkInstrumentation.Companion.ZERO_SAMPLE_RATE
import com.openobserve.android.trace.internal._TraceInternalProxy.propagationHelper
import java.util.Locale

internal val FeatureSdkCore?.isRumEnabled: Boolean
    get() = this?.getFeature(Feature.RUM_FEATURE_NAME) != null

internal fun OpenObserveSpan.applyPriority(isSampled: Boolean, traceSampler: Sampler<OpenObserveSpan>) {
    val samplingPriority = if (isSampled) {
        PrioritySampling.SAMPLER_KEEP
    } else {
        PrioritySampling.SAMPLER_DROP
    }

    val spanContext = context()
    if (spanContext.setSamplingPriority(samplingPriority)) {
        spanContext.setMetric(
            AGENT_PSR_ATTRIBUTE,
            (traceSampler.getSampleRate() ?: ZERO_SAMPLE_RATE) / ALL_IN_SAMPLE_RATE
        )
    }
}

internal fun OpenObserveSpan.sample(request: HttpRequestInfo, traceSampler: Sampler<OpenObserveSpan>): Boolean {
    val samplingPriority = samplingPriority
    return if (samplingPriority != null) {
        samplingPriority > 0
    } else {
        propagationHelper.extractSamplingDecision(request) ?: traceSampler.sample(this)
    }
}

internal fun OpenObserveSpan.finishRumAware(isSampled: Boolean, canSendSpan: Boolean) {
    if (canSendSpan && isSampled) {
        finish()
    } else {
        drop()
    }
}

internal fun OpenObserveTracer.buildSpan(
    request: HttpRequestInfo,
    networkInstrumentationName: String,
    traceOrigin: String?
): OpenObserveSpan {
    val parentContext = propagationHelper.extractParentContext(this, request)

    val span = buildSpan(SPAN_NAME.format(Locale.US, networkInstrumentationName))
        .withOrigin(traceOrigin)
        .withParentContext(parentContext)
        .start()

    span.resourceName = request.url.substringBefore(URL_QUERY_PARAMS_BLOCK_SEPARATOR)
    span.setTag(Tags.KEY_HTTP_URL, request.url)
    span.setTag(Tags.KEY_HTTP_METHOD, request.method)
    span.setTag(Tags.KEY_SPAN_KIND, Tags.VALUE_SPAN_KIND_CLIENT)

    return span
}
