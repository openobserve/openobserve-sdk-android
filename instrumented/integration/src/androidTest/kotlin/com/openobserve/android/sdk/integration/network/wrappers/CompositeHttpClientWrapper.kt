/*
 * Unless explicitly stated otherwise all files in this repository are licensed under the Apache License Version 2.0.
 * This product includes software developed at OpenObserve (https://www.openobserve.ai/).
 * Copyright 2016-Present OpenObserve, Inc.
 */

package com.openobserve.android.sdk.integration.network.wrappers

import com.openobserve.android.sdk.integration.network.models.ClientExecutionResult
import com.openobserve.android.sdk.integration.network.models.TestRequest

internal class CompositeHttpClientWrapper(
    private val clients: List<HttpTestClientWrapper>
) {
    suspend fun execute(request: TestRequest): Map<String, ClientExecutionResult> =
        clients.associate { client -> client.name to client.execute(request) }

    fun shutdown() = clients.forEach { it.shutdown() }
}
