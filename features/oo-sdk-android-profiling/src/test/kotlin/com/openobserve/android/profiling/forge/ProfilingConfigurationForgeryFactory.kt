/*
 * Unless explicitly stated otherwise all files in this repository are licensed under the Apache License Version 2.0.
 * This product includes software developed at OpenObserve (https://www.openobserve.ai/).
 * Copyright 2016-Present OpenObserve, Inc.
 */

package com.openobserve.android.profiling.forge

import com.openobserve.android.profiling.ExperimentalProfilingApi
import com.openobserve.android.profiling.ProfilingConfiguration
import fr.xgouchet.elmyr.Forge
import fr.xgouchet.elmyr.ForgeryFactory

@OptIn(ExperimentalProfilingApi::class)
class ProfilingConfigurationForgeryFactory :
    ForgeryFactory<ProfilingConfiguration> {
    override fun getForgery(forge: Forge): ProfilingConfiguration {
        return ProfilingConfiguration(
            sampleRate = forge.aFloat(min = 0f, max = 100f),
            customEndpointUrl = forge.aNullable {
                aStringMatching("http(s?)://[a-z]+\\.com/\\w+")
            }
        )
    }
}
