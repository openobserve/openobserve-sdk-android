/*
 * Unless explicitly stated otherwise all files in this repository are licensed under the Apache License Version 2.0.
 * This product includes software developed at OpenObserve (https://www.openobserve.ai/).
 * Copyright 2016-Present OpenObserve, Inc.
 */

package com.openobserve.android.coil

import android.net.Uri
import coil.request.ImageRequest
import com.openobserve.android.OpenObserve
import com.openobserve.android.api.SdkCore
import com.openobserve.android.rum.GlobalRumMonitor
import com.openobserve.android.rum.RumErrorSource
import okhttp3.HttpUrl
import java.io.File

/**
 * Provides an implementation of [coil.request.ImageRequest.Listener] already set up to send relevant information
 * to OpenObserve.
 *
 * It will automatically send RUM error events whenever a Coil [ImageRequest]
 * throws any [Exception].
 *
 * @param sdkCore SDK instance to use for the reporting. If not provided, default instance
 * will be used.
 */
class OpenObserveCoilRequestListener
@JvmOverloads
constructor(
    private val sdkCore: SdkCore = OpenObserve.getInstance()
) : ImageRequest.Listener {

    // region Listener

    /** @inheritDoc */
    override fun onError(request: ImageRequest, throwable: Throwable) {
        GlobalRumMonitor.get(sdkCore).addError(
            REQUEST_ERROR_MESSAGE,
            RumErrorSource.SOURCE,
            throwable,
            extractRequestAttributes(request)
        )
    }

    // endregion

    // region Internals

    private fun extractRequestAttributes(request: ImageRequest): Map<String, Any?> {
        return when (request.data) {
            is String -> {
                mapOf(
                    REQUEST_PATH_TAG to request.data as String
                )
            }
            is Uri -> {
                mapOf(
                    REQUEST_PATH_TAG to (request.data as Uri).path
                )
            }
            is HttpUrl -> {
                @Suppress("UnsafeThirdPartyFunctionCall") // valid HTTP url during toUrl call
                val urlValue = (request.data as HttpUrl).toUrl().toString()
                mapOf(
                    REQUEST_PATH_TAG to urlValue
                )
            }
            is File -> {
                mapOf(
                    REQUEST_PATH_TAG to (request.data as File).path
                )
            }
            else -> {
                emptyMap()
            }
        }
    }

    // endregion

    internal companion object {
        internal const val REQUEST_ERROR_MESSAGE = "Coil request error"
        internal const val REQUEST_PATH_TAG = "request_path"
    }
}
