/*
 * Unless explicitly stated otherwise all files in this repository are licensed under the Apache License Version 2.0.
 * This product includes software developed at OpenObserve (https://www.openobserve.ai/).
 * Copyright 2016-Present OpenObserve, Inc.
 */
package com.openobserve.android.sdk.integration.network.models

data class TestRequest(
    val url: String,
    val method: String,
    val headers: Map<String, List<String>> = emptyMap(),
    val body: String? = null
)
