/*
 * Unless explicitly stated otherwise all files in this repository are licensed under the Apache License Version 2.0.
 * This product includes software developed at OpenObserve (https://www.openobserve.ai/).
 * Copyright 2016-Present OpenObserve, Inc.
 */

package com.openobserve.android.rum.utils.forge

import com.openobserve.android.rum.internal.domain.event.ResourceTiming
import fr.xgouchet.elmyr.Forge
import fr.xgouchet.elmyr.ForgeryFactory

class ResourceTimingForgeryFactory :
    ForgeryFactory<ResourceTiming> {
    override fun getForgery(forge: Forge): ResourceTiming {
        return ResourceTiming(
            dnsStart = forge.aPositiveLong(),
            dnsDuration = forge.aPositiveLong(),
            connectStart = forge.aPositiveLong(),
            connectDuration = forge.aPositiveLong(),
            sslStart = forge.aPositiveLong(),
            sslDuration = forge.aPositiveLong(),
            firstByteStart = forge.aPositiveLong(),
            firstByteDuration = forge.aPositiveLong(),
            downloadStart = forge.aPositiveLong(),
            downloadDuration = forge.aPositiveLong()
        )
    }
}
