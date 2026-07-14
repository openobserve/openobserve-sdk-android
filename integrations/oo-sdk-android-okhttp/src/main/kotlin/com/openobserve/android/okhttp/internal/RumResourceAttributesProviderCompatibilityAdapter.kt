/*
 * Unless explicitly stated otherwise all files in this repository are licensed under the Apache License Version 2.0.
 * This product includes software developed at OpenObserve (https://www.openobserve.ai/).
 * Copyright 2016-Present OpenObserve, Inc.
 */

package com.openobserve.android.okhttp.internal

import com.openobserve.android.api.InternalLogger
import com.openobserve.android.api.feature.FeatureSdkCore
import com.openobserve.android.api.instrumentation.network.HttpRequestInfo
import com.openobserve.android.api.instrumentation.network.HttpResponseInfo
import com.openobserve.android.core.SdkReference
import com.openobserve.android.rum.RumResourceAttributesProvider
import okhttp3.Request
import okhttp3.Response

internal class RumResourceAttributesProviderCompatibilityAdapter(
    internal val delegate: RumResourceAttributesProvider,
    internal val sdkReference: SdkReference
) : RumResourceAttributesProvider by delegate {

    private val internalLogger: InternalLogger
        get() = (sdkReference.get() as? FeatureSdkCore)?.internalLogger ?: InternalLogger.UNBOUND

    @Deprecated(
        "Use the variant with HttpRequestInfo/HttpResponseInfo instead",
        replaceWith = ReplaceWith(
            "onProvideAttributes(OkHttpHttpRequestInfo(request), " +
                "OkHttpHttpResponseInfo(response, internalLogger), throwable)"
        )
    )
    override fun onProvideAttributes(
        request: Request,
        response: Response?,
        throwable: Throwable?
    ): Map<String, Any?> {
        @Suppress("DEPRECATION")
        return delegate.onProvideAttributes(request, response, throwable).ifEmpty {
            delegate.onProvideAttributes(
                request.toHttpRequestInfo(),
                response?.toHttpResponseInfo(internalLogger),
                throwable
            )
        }
    }

    override fun onProvideAttributes(
        request: HttpRequestInfo,
        response: HttpResponseInfo?,
        throwable: Throwable?
    ): Map<String, Any?> {
        return delegate.onProvideAttributes(request, response, throwable).ifEmpty {
            @Suppress("DEPRECATION")
            delegate.onProvideAttributes(
                request = request.toOkHttpRequest() ?: return emptyMap(),
                response = response?.toOkHttpResponse(),
                throwable = throwable
            )
        }
    }
}
