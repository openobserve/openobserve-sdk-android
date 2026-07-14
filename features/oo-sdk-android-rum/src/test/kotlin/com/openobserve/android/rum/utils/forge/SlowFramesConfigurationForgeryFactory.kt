/*
 * Unless explicitly stated otherwise all files in this repository are licensed under the Apache License Version 2.0.
 * This product includes software developed at OpenObserve (https://www.openobserve.ai/).
 * Copyright 2016-Present OpenObserve, Inc.
 */

package com.openobserve.android.rum.utils.forge

import com.openobserve.android.rum.configuration.SlowFramesConfiguration
import fr.xgouchet.elmyr.Forge
import fr.xgouchet.elmyr.ForgeryFactory

internal class SlowFramesConfigurationForgeryFactory : ForgeryFactory<SlowFramesConfiguration> {
    override fun getForgery(forge: Forge) = SlowFramesConfiguration(
        maxSlowFramesAmount = forge.anInt(min = 0),
        maxSlowFrameThresholdNs = forge.aLong(min = 0),
        continuousSlowFrameThresholdNs = forge.aLong(min = 0),
        freezeDurationThresholdNs = forge.aLong(min = 0),
        minViewLifetimeThresholdNs = forge.aLong(min = 0)
    )
}
