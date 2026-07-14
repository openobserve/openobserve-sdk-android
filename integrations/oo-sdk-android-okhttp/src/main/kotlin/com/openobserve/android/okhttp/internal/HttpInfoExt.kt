/*
 * Unless explicitly stated otherwise all files in this repository are licensed under the Apache License Version 2.0.
 * This product includes software developed at OpenObserve (https://www.openobserve.ai/).
 * Copyright 2016-Present OpenObserve, Inc.
 */
package com.openobserve.android.okhttp.internal

import com.openobserve.android.api.InternalLogger
import com.openobserve.android.api.instrumentation.network.HttpRequestInfo
import com.openobserve.android.api.instrumentation.network.HttpResponseInfo
import okhttp3.Request
import okhttp3.Response

internal fun Request.toHttpRequestInfo() = OkHttpRequestInfo(this)
internal fun HttpRequestInfo.toOkHttpRequest(): Request? = (this as? OkHttpRequestInfo)?.originalRequest
internal fun HttpResponseInfo.toOkHttpResponse(): Response? = (this as? OkHttpResponseInfo)?.originalResponse
internal fun Response.toHttpResponseInfo(internalLogger: InternalLogger) = OkHttpResponseInfo(this, internalLogger)
