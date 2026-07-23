/*
 * Unless explicitly stated otherwise all files in this repository are licensed under the Apache License Version 2.0.
 * This product includes software developed at Datadog (https://www.datadoghq.com/).
 * Copyright 2016-Present Datadog, Inc.
 */

package com.openobserve.opentelemetry.trace;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.openobserve.android.api.InternalLogger;
import com.openobserve.android.trace.api.OpenObserveTracingConstants;
import com.openobserve.android.trace.api.span.OpenObserveSpanContext;
import com.openobserve.android.trace.api.trace.OpenObserveTraceId;
import com.openobserve.android.trace.internal.OpenObserveTraceExtKt;
import com.openobserve.android.trace.internal._TraceInternalProxy;

import java.util.Collections;
import java.util.Locale;
import java.util.Map;

import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.SpanContext;
import io.opentelemetry.context.Context;

public class OtelExtractedContext implements OpenObserveSpanContext {
    private final OpenObserveTraceId traceId;
    private final long spanId;
    private final int prioritySampling;

    private OtelExtractedContext(SpanContext context) {
        traceId = OpenObserveTraceExtKt.fromHex(OpenObserveTraceId.Companion, context.getTraceId());
        spanId = _TraceInternalProxy.spanIdConverter.fromHex(context.getSpanId());
        prioritySampling = context.isSampled()
                ? OpenObserveTracingConstants.PrioritySampling.SAMPLER_KEEP
                : OpenObserveTracingConstants.PrioritySampling.UNSET;
    }

    public static OpenObserveSpanContext extract(Context context, InternalLogger logger) {
        Span span = Span.fromContext(context);
        SpanContext spanContext = span.getSpanContext();
        if (spanContext instanceof OtelSpanContext) {
            return ((OtelSpanContext) spanContext).delegate;
        } else if (spanContext.isValid()) {
            try {
                return new OtelExtractedContext(spanContext);
            } catch (NumberFormatException e) {
                logger.log(
                        InternalLogger.Level.DEBUG,
                        InternalLogger.Target.MAINTAINER,
                        () -> String.format(Locale.US, "Failed to convert span context with trace id = " +
                                "{%1s} and span id = {%2s}", spanContext.getSpanId(), spanContext.getTraceId()),
                        null,
                        false,
                        null);

            }
        }
        return null;
    }

    @NonNull
    @Override
    public OpenObserveTraceId getTraceId() {
        return traceId;
    }

    @Override
    public long getSpanId() {
        return spanId;
    }

    @Override
    public int getSamplingPriority() {
        return prioritySampling;
    }

    @NonNull
    @Override
    public Map<String, Object> getTags() {
        // Do nothing
        return Collections.emptyMap();
    }

    @Override
    public boolean setSamplingPriority(int samplingPriority) {
        // Do nothing
        return false;
    }

    @Override
    public void setMetric(@Nullable CharSequence key, double value) {
        // Do nothing
    }
}
