/*
 * Unless explicitly stated otherwise all files in this repository are licensed under the Apache License Version 2.0.
 * This product includes software developed at Datadog (https://www.datadoghq.com/).
 * Copyright 2016-Present Datadog, Inc.
 */
package com.openobserve.android.sdk.integration.network.models

import com.openobserve.android.trace.api.span.OpenObserveSpan

internal data class ClientExecutionResult(
    val name: String,
    val request: TestRequest?,
    val response: TestResponse?,
    val collectedSpans: List<OpenObserveSpan>,
    val error: Throwable?
)
