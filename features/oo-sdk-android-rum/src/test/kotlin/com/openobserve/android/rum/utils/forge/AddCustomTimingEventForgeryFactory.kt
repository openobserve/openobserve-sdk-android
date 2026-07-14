/*
 * Unless explicitly stated otherwise all files in this repository are licensed under the Apache License Version 2.0.
 * This product includes software developed at OpenObserve (https://www.openobserve.ai/).
 * Copyright 2016-Present OpenObserve, Inc.
 */
package com.openobserve.android.rum.utils.forge

import com.openobserve.android.rum.internal.domain.scope.RumRawEvent
import fr.xgouchet.elmyr.Forge
import fr.xgouchet.elmyr.ForgeryFactory

internal class AddCustomTimingEventForgeryFactory : ForgeryFactory<RumRawEvent.AddCustomTiming> {
    override fun getForgery(forge: Forge): RumRawEvent.AddCustomTiming {
        return RumRawEvent.AddCustomTiming(
            name = forge.aString()
        )
    }
}
