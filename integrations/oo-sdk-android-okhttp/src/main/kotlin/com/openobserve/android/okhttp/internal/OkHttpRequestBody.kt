/*
 * Unless explicitly stated otherwise all files in this repository are licensed under the Apache License Version 2.0.
 * This product includes software developed at OpenObserve (https://www.openobserve.ai/).
 * Copyright 2016-Present OpenObserve, Inc.
 */
package com.openobserve.android.okhttp.internal

import com.openobserve.android.api.instrumentation.network.HttpRequestBody
import okhttp3.RequestBody

internal data class OkHttpRequestBody(val body: RequestBody) : HttpRequestBody
