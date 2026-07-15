/*
 * Unless explicitly stated otherwise all files in this repository are licensed under the Apache License Version 2.0.
 * This product includes software developed at Datadog (https://www.datadoghq.com/).
 * Copyright 2016-Present Datadog, Inc.
 */

package com.openobserve.android.okhttp.trace

import com.openobserve.android.trace.api.span.OpenObserveSpan
import okhttp3.Request

/**
 * Set the parent for the [OpenObserveSpan] created around this OkHttp [Request].
 * @param span the parent [OpenObserveSpan]
 */
fun Request.Builder.parentSpan(span: OpenObserveSpan): Request.Builder {
    @Suppress("UnsafeThirdPartyFunctionCall") // Span can't be null
    tag(OpenObserveSpan::class.java, span)
    return this
}
