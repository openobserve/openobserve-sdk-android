/*
 * Unless explicitly stated otherwise all files in this repository are licensed under the Apache License Version 2.0.
 * This product includes software developed at OpenObserve (https://www.openobserve.ai/).
 * Copyright 2016-Present OpenObserve, Inc.
 */

package com.openobserve.android.sdk.integration.network.wrappers

import com.openobserve.android.sdk.integration.network.models.ClientExecutionResult
import com.openobserve.android.sdk.integration.network.models.TestRequest
import com.openobserve.android.trace.TracingHeaderType

internal interface HttpTestClientWrapper {
    val name: String

    fun shutdown()

    suspend fun execute(request: TestRequest): ClientExecutionResult

    companion object {
        val tracedHosts = mapOf<String, Set<TracingHeaderType>>(
            "localhost" to setOf(TracingHeaderType.OPENOBSERVE, TracingHeaderType.TRACECONTEXT),
            "127.0.0.1" to setOf(TracingHeaderType.OPENOBSERVE, TracingHeaderType.TRACECONTEXT)
        )
    }
}
