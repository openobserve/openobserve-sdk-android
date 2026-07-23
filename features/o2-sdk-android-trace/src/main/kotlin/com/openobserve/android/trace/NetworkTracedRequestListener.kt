/*
 * Unless explicitly stated otherwise all files in this repository are licensed under the Apache License Version 2.0.
 * This product includes software developed at Datadog (https://www.datadoghq.com/).
 * Copyright 2016-Present Datadog, Inc.
 */

package com.openobserve.android.trace

import com.openobserve.android.api.instrumentation.network.HttpRequestInfo
import com.openobserve.android.api.instrumentation.network.HttpResponseInfo
import com.openobserve.android.trace.api.span.OpenObserveSpan
import com.openobserve.tools.annotation.NoOpImplementation

/**
 * Listener for automatically created [com.openobserve.android.trace.api.span.OpenObserveSpan] around [com.openobserve.android.api.instrumentation.network.HttpRequestInfo].
 */
@NoOpImplementation
interface NetworkTracedRequestListener {
    /**
     * Notifies that a span was automatically created around an OkHttp [com.openobserve.android.api.instrumentation.network.HttpRequestInfo].
     * You can update the given [com.openobserve.android.trace.api.span.OpenObserveSpan] (e.g.: add custom tags / baggage items) before it
     * is persisted. Won't be called if [com.openobserve.android.api.instrumentation.network.HttpRequestInfo] wasn't sampled.
     * @param request the intercepted [com.openobserve.android.api.instrumentation.network.HttpRequestInfo]
     * @param span the [com.openobserve.android.trace.api.span.OpenObserveSpan] created around the intercepted [com.openobserve.android.api.instrumentation.network.HttpRequestInfo]
     * @param response the [com.openobserve.android.api.instrumentation.network.HttpRequestInfo] response in case of any
     * @param throwable in case an error occurred during the [com.openobserve.android.api.instrumentation.network.HttpRequestInfo]
     */
    fun onRequestIntercepted(
        request: HttpRequestInfo,
        span: OpenObserveSpan,
        response: HttpResponseInfo?,
        throwable: Throwable?
    )
}
