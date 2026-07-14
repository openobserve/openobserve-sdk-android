/*
 * Unless explicitly stated otherwise all files in this repository are licensed under the Apache License Version 2.0.
 * This product includes software developed at OpenObserve (https://www.openobserve.ai/).
 * Copyright 2016-Present OpenObserve, Inc.
 */

package com.openobserve.android.sessionreplay.compose.internal.utils

import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.asAndroidPath
import com.openobserve.android.api.InternalLogger

internal class PathUtils(
    private val logger: InternalLogger
) {
    internal fun asAndroidPathSafe(path: Path): android.graphics.Path? {
        return try {
            @Suppress("UnsafeThirdPartyFunctionCall") // handling UnsupportedOperationException
            path.asAndroidPath()
        } catch (e: UnsupportedOperationException) {
            logger.log(
                target = InternalLogger.Target.MAINTAINER,
                level = InternalLogger.Level.WARN,
                messageBuilder = { PATH_CONVERSION_ERROR },
                throwable = e
            )
            null
        }
    }

    internal companion object {
        internal const val PATH_CONVERSION_ERROR = "Failed to convert Compose Path to Android Path"
    }
}
