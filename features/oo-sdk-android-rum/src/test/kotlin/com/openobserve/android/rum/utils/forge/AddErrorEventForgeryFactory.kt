/*
 * Unless explicitly stated otherwise all files in this repository are licensed under the Apache License Version 2.0.
 * This product includes software developed at OpenObserve (https://www.openobserve.ai/).
 * Copyright 2016-Present OpenObserve, Inc.
 */
package com.openobserve.android.rum.utils.forge

import com.openobserve.android.rum.RumErrorSource
import com.openobserve.android.rum.internal.RumErrorSourceType
import com.openobserve.android.rum.internal.domain.scope.RumRawEvent
import com.openobserve.tools.unit.forge.aThrowable
import com.openobserve.tools.unit.forge.exhaustiveAttributes
import fr.xgouchet.elmyr.Forge
import fr.xgouchet.elmyr.ForgeryFactory

internal class AddErrorEventForgeryFactory : ForgeryFactory<RumRawEvent.AddError> {
    override fun getForgery(forge: Forge) = RumRawEvent.AddError(
        message = forge.aString(),
        sourceType = forge.aValueFrom(RumErrorSourceType::class.java),
        source = forge.aValueFrom(RumErrorSource::class.java),
        throwable = forge.aNullable { aThrowable() },
        stacktrace = forge.aNullable { aString() },
        isFatal = forge.aBool(),
        attributes = forge.exhaustiveAttributes(),
        type = forge.aNullable { aString() },
        threads = forge.aList { getForgery() },
        timeSinceAppStartNs = forge.aNullable { aPositiveLong() }
    )
}
