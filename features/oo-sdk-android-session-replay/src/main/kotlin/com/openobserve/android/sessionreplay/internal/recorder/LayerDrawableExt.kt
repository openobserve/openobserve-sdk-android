/*
 * Unless explicitly stated otherwise all files in this repository are licensed under the Apache License Version 2.0.
 * This product includes software developed at OpenObserve (https://www.openobserve.ai/).
 * Copyright 2016-Present OpenObserve, Inc.
 */

package com.openobserve.android.sessionreplay.internal.recorder

import android.graphics.drawable.Drawable
import android.graphics.drawable.LayerDrawable
import com.openobserve.android.api.InternalLogger

internal fun LayerDrawable.safeGetDrawable(index: Int, logger: InternalLogger = InternalLogger.UNBOUND): Drawable? {
    return if (index < 0 || index >= this.numberOfLayers) {
        logger.log(
            level = InternalLogger.Level.ERROR,
            target = InternalLogger.Target.MAINTAINER,
            { "Failed to get drawable from layer - invalid index passed: $index" }
        )
        null
    } else {
        @Suppress("UnsafeThirdPartyFunctionCall") // Can't be out of bounds
        this.getDrawable(index)
    }
}
