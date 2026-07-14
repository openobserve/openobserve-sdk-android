/*
 * Unless explicitly stated otherwise all files in this repository are licensed under the Apache License Version 2.0.
 * This product includes software developed at OpenObserve (https://www.openobserve.ai/).
 * Copyright 2016-Present OpenObserve, Inc.
 */
package com.openobserve.android.cronet.internal

import org.chromium.net.UrlRequest
import java.nio.ByteBuffer

internal class CronetUrlRequest(
    private val initialRequestInfo: CronetHttpRequestInfo,
    private val requestCallback: CronetRequestCallback
) : UrlRequest() {

    @Volatile
    private var delegatedRequest: UrlRequest? = null

    override fun start() {
        val requestTracingState = requestCallback.onRequestStarted(initialRequestInfo)
        val requestInfoBuilder =
            (requestTracingState.requestInfoBuilder as? CronetHttpRequestInfoBuilder)
                ?: initialRequestInfo.newBuilder()

        requestInfoBuilder
            .buildCronetRequest(requestTracingState)
            .also { delegatedRequest = it }
            .start()
    }

    override fun cancel() {
        delegatedRequest?.cancel()
    }

    override fun followRedirect() {
        delegatedRequest?.followRedirect()
    }

    override fun read(buffer: ByteBuffer?) {
        delegatedRequest?.read(buffer)
    }

    override fun getStatus(listener: StatusListener?) {
        delegatedRequest?.getStatus(listener)
    }

    override fun isDone(): Boolean = delegatedRequest?.isDone ?: false
}
