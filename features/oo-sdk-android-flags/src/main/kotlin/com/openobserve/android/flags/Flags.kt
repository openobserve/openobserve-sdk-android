/*
 * Unless explicitly stated otherwise all files in this repository are licensed under the Apache License Version 2.0.
 * This product includes software developed at Datadog (https://www.datadoghq.com/).
 * Copyright 2016-Present Datadog, Inc.
 */

package com.openobserve.android.flags

import com.openobserve.android.OpenObserve
import com.openobserve.android.api.SdkCore
import com.openobserve.android.api.feature.FeatureSdkCore
import com.openobserve.android.flags.internal.EvaluationsFeature
import com.openobserve.android.flags.internal.FlagsFeature

/**
 * Entry point for the Flags feature.
 */
object Flags {

    /**
     * Enables the Flags feature.
     *
     * @param configuration configuration to use with feature flags and experiments. If not provided, the default
     * configuration will be used.
     * @param sdkCore SDK instance to register feature in. If not provided, a default SDK instance
     * will be used.
     */
    @JvmOverloads
    @JvmStatic
    fun enable(
        configuration: FlagsConfiguration = FlagsConfiguration.default,
        sdkCore: SdkCore = OpenObserve.getInstance()
    ) {
        if (configuration.trackEvaluations) {
            val evaluationsFeature = EvaluationsFeature(
                sdkCore = sdkCore as FeatureSdkCore,
                flagsConfiguration = configuration
            )
            sdkCore.registerFeature(evaluationsFeature)
        }

        val flagsFeature = FlagsFeature(
            sdkCore = sdkCore as FeatureSdkCore,
            flagsConfiguration = configuration
        )
        sdkCore.registerFeature(flagsFeature)
    }
}
