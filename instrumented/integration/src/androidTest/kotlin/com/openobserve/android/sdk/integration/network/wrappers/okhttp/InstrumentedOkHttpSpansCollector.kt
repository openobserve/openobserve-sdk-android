/*
 * Unless explicitly stated otherwise all files in this repository are licensed under the Apache License Version 2.0.
 * This product includes software developed at Datadog (https://www.datadoghq.com/).
 * Copyright 2016-Present Datadog, Inc.
 */
package com.openobserve.android.sdk.integration.network.wrappers.okhttp

import com.openobserve.android.api.instrumentation.network.HttpRequestInfo
import com.openobserve.android.api.instrumentation.network.HttpResponseInfo
import com.openobserve.android.trace.NetworkTracedRequestListener
import com.openobserve.android.trace.api.span.OpenObserveSpan
import java.util.concurrent.CopyOnWriteArrayList

internal class InstrumentedOkHttpSpansCollector : NetworkTracedRequestListener {
    val spans = CopyOnWriteArrayList<OpenObserveSpan>()
    override fun onRequestIntercepted(
        request: HttpRequestInfo,
        span: OpenObserveSpan,
        response: HttpResponseInfo?,
        throwable: Throwable?
    ) {
        spans.add(span)
    }
}
