/*
 * Unless explicitly stated otherwise all files in this repository are licensed under the Apache License Version 2.0.
 * This product includes software developed at Datadog (https://www.datadoghq.com/).
 * Copyright 2016-Present Datadog, Inc.
 */
package com.openobserve.android.trace.internal

import com.openobserve.android.trace.api.span.OpenObserveSpanLink
import com.openobserve.trace.api.DDTraceId
import com.openobserve.trace.bootstrap.instrumentation.api.AgentSpanLink
import com.openobserve.trace.bootstrap.instrumentation.api.SpanLink
import com.openobserve.trace.bootstrap.instrumentation.api.SpanLinkAttributes

internal class OpenObserveSpanLinkAdapter(delegate: OpenObserveSpanLink) :
    SpanLink(
        /* traceId */
        DDTraceId.fromHex(delegate.traceId.toHexString()),
        /* spanId */
        delegate.spanId,
        /* traceFlags */
        if (delegate.sampled) AgentSpanLink.SAMPLED_FLAG else AgentSpanLink.DEFAULT_FLAGS,
        /* traceState */
        delegate.traceStrace,
        /* attributes */
        SpanLinkAttributes.fromMap(delegate.attributes)
    )
