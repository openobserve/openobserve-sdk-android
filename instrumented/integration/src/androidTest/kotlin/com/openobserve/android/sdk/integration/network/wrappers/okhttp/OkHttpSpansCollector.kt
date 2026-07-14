/*
 * Unless explicitly stated otherwise all files in this repository are licensed under the Apache License Version 2.0.
 * This product includes software developed at OpenObserve (https://www.openobserve.ai/).
 * Copyright 2016-Present OpenObserve, Inc.
 */
package com.openobserve.android.sdk.integration.network.wrappers.okhttp

import com.openobserve.android.okhttp.trace.TracedRequestListener
import com.openobserve.android.trace.api.span.OpenObserveSpan
import okhttp3.Request
import okhttp3.Response
import java.util.concurrent.CopyOnWriteArrayList

class OkHttpSpansCollector : TracedRequestListener {

    val spans = CopyOnWriteArrayList<OpenObserveSpan>()

    override fun onRequestIntercepted(
        request: Request,
        span: OpenObserveSpan,
        response: Response?,
        throwable: Throwable?
    ) {
        spans.add(span)
    }
}
