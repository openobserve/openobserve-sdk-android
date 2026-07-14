/*
 * Unless explicitly stated otherwise all files in this repository are licensed under the Apache License Version 2.0.
 * This product includes software developed at OpenObserve (https://www.openobserve.ai/).
 * Copyright 2016-Present OpenObserve, Inc.
 */

package com.openobserve.android.rum.internal.startup

import android.app.Activity
import android.os.Handler
import com.openobserve.android.api.InternalLogger
import com.openobserve.android.rum.internal.utils.window.RumWindowCallbacksRegistry

internal class RumFirstDrawTimeReporterImpl(
    private val internalLogger: InternalLogger,
    private val timeProviderNs: () -> Long,
    private val windowCallbacksRegistry: RumWindowCallbacksRegistry,
    private val handler: Handler
) : RumFirstDrawTimeReporter {

    override fun subscribeToFirstFrameDrawn(
        activity: Activity,
        callback: RumFirstDrawTimeReporter.Callback
    ): RumFirstDrawTimeReporter.Handle {
        return RumFirstDrawTimeReporterHandleImpl(
            callback = callback,
            activity = activity,
            internalLogger = internalLogger,
            timeProviderNs = timeProviderNs,
            windowCallbacksRegistry = windowCallbacksRegistry,
            handler = handler
        )
    }
}
