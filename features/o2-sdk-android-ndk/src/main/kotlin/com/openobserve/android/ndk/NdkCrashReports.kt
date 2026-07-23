/*
 * Unless explicitly stated otherwise all files in this repository are licensed under the Apache License Version 2.0.
 * This product includes software developed at Datadog (https://www.datadoghq.com/).
 * Copyright 2016-Present Datadog, Inc.
 */

package com.openobserve.android.ndk

import com.openobserve.android.OpenObserve
import com.openobserve.android.api.SdkCore
import com.openobserve.android.api.feature.FeatureSdkCore
import com.openobserve.android.ndk.internal.NdkCrashReportsFeature

/**
 * An entry point to OpenObserve NDK Crash Reports feature.
 */
object NdkCrashReports {

    /**
     * Enables a NDK Crash Reports feature.
     *
     * @param sdkCore SDK instance to register feature in. If not provided, default SDK instance
     * will be used.
     */
    @JvmOverloads
    @JvmStatic
    fun enable(sdkCore: SdkCore = OpenObserve.getInstance()) {
        val ndkCrashReportsFeature = NdkCrashReportsFeature(sdkCore as FeatureSdkCore)

        sdkCore.registerFeature(ndkCrashReportsFeature)
    }
}
