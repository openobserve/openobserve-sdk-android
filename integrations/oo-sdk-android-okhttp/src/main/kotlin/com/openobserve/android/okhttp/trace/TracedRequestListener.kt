/*
 * Unless explicitly stated otherwise all files in this repository are licensed under the Apache License Version 2.0.
 * This product includes software developed at OpenObserve (https://www.openobserve.ai/).
 * Copyright 2016-Present OpenObserve, Inc.
 */

package com.openobserve.android.okhttp.trace

import com.openobserve.android.trace.api.span.OpenObserveSpan
import com.openobserve.tools.annotation.NoOpImplementation
import okhttp3.Request
import okhttp3.Response

/**
 * Listener for automatically created [OpenObserveSpan] around OkHttp [Request].
 */
@NoOpImplementation
interface TracedRequestListener {

    /**
     * Notifies that a span was automatically created around an OkHttp [Request].
     * You can update the given [OpenObserveSpan] (e.g.: add custom tags / baggage items) before it
     * is persisted. Won't be called if [Request] wasn't sampled.
     * @param request the intercepted [Request]
     * @param span the [OpenObserveSpan] created around the intercepted [Request]
     * @param response the [Request] response in case of any
     * @param throwable in case an error occurred during the [Request]
     */
    fun onRequestIntercepted(
        request: Request,
        span: OpenObserveSpan,
        response: Response?,
        throwable: Throwable?
    )
}
