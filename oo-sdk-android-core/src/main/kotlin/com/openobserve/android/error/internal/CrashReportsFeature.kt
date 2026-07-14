/*
 * Unless explicitly stated otherwise all files in this repository are licensed under the Apache License Version 2.0.
 * This product includes software developed at OpenObserve (https://www.openobserve.ai/).
 * Copyright 2016-Present OpenObserve, Inc.
 */

package com.openobserve.android.error.internal

import android.content.Context
import com.openobserve.android.api.feature.Feature
import com.openobserve.android.api.feature.FeatureSdkCore
import java.util.concurrent.atomic.AtomicBoolean

internal class CrashReportsFeature(private val sdkCore: FeatureSdkCore) : Feature {

    internal val initialized = AtomicBoolean(false)
    internal var originalUncaughtExceptionHandler = Thread.getDefaultUncaughtExceptionHandler()

    // region Feature

    override val name: String = CRASH_FEATURE_NAME

    override fun onInitialize(appContext: Context) {
        setupExceptionHandler(appContext)
        initialized.set(true)
    }

    override fun onStop() {
        resetOriginalExceptionHandler()
        initialized.set(false)
    }

    // endregion

    // region Internal

    private fun setupExceptionHandler(
        appContext: Context
    ) {
        originalUncaughtExceptionHandler = Thread.getDefaultUncaughtExceptionHandler()
        OpenObserveExceptionHandler(
            sdkCore = sdkCore,
            appContext = appContext
        ).register()
    }

    private fun resetOriginalExceptionHandler() {
        Thread.setDefaultUncaughtExceptionHandler(originalUncaughtExceptionHandler)
    }

    // endregion

    companion object {
        internal const val CRASH_FEATURE_NAME = "crash"
    }
}
