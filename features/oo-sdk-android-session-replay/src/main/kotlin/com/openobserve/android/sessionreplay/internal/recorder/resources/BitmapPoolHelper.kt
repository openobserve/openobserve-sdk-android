/*
 * Unless explicitly stated otherwise all files in this repository are licensed under the Apache License Version 2.0.
 * This product includes software developed at OpenObserve (https://www.openobserve.ai/).
 * Copyright 2016-Present OpenObserve, Inc.
 */

package com.openobserve.android.sessionreplay.internal.recorder.resources

import android.graphics.Bitmap
import com.openobserve.android.sessionreplay.internal.utils.InvocationUtils

internal class BitmapPoolHelper(
    private val invocationUtils: InvocationUtils = InvocationUtils()
) {
    internal fun generateKey(bitmap: Bitmap): String =
        generateKey(bitmap.width, bitmap.height, bitmap.config)

    internal fun generateKey(width: Int, height: Int, config: Bitmap.Config?): String =
        "$width-$height-$config"

    internal fun <R> safeCall(call: () -> R): R? =
        invocationUtils.safeCallWithErrorLogging(
            call = { call() },
            failureMessage = BITMAP_OPERATION_FAILED
        )

    private companion object {
        private const val BITMAP_OPERATION_FAILED = "operation failed for bitmap pool"
    }
}
