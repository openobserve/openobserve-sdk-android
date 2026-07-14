/*
 * Unless explicitly stated otherwise all files in this repository are licensed under the Apache License Version 2.0.
 * This product includes software developed at OpenObserve (https://www.openobserve.ai/).
 * Copyright 2016-Present OpenObserve, Inc.
 */

package com.openobserve.android.sessionreplay.internal.recorder.callback

import android.view.MotionEvent
import com.openobserve.android.internal.system.BuildSdkVersionProvider

internal object MotionEventUtils {

    // For Android SDK < 29 we will have to know the target View on which this event happened
    // and then to compute the absolute as target.absoluteX,Y + event.getX,Y(pointerIndex)
    // This will not be handled now as it is too complex and not very optimised. For now we will
    // not support multi finger gestures in the player for version < 29.
    fun getPointerAbsoluteX(
        event: MotionEvent,
        pointerIndex: Int,
        buildSdkVersionProvider: BuildSdkVersionProvider = BuildSdkVersionProvider.DEFAULT
    ): Float {
        return if (buildSdkVersionProvider.isAtLeastQ) {
            event.getRawX(pointerIndex)
        } else {
            return event.rawX
        }
    }

    fun getPointerAbsoluteY(
        event: MotionEvent,
        pointerIndex: Int,
        buildSdkVersionProvider: BuildSdkVersionProvider = BuildSdkVersionProvider.DEFAULT
    ): Float {
        return if (buildSdkVersionProvider.isAtLeastQ) {
            event.getRawY(pointerIndex)
        } else {
            return event.rawY
        }
    }
}
