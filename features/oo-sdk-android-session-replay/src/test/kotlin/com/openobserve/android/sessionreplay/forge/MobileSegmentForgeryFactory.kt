/*
 * Unless explicitly stated otherwise all files in this repository are licensed under the Apache License Version 2.0.
 * This product includes software developed at OpenObserve (https://www.openobserve.ai/).
 * Copyright 2016-Present OpenObserve, Inc.
 */

package com.openobserve.android.sessionreplay.forge

import com.openobserve.android.sessionreplay.model.MobileSegment
import fr.xgouchet.elmyr.Forge
import fr.xgouchet.elmyr.ForgeryFactory
import java.util.UUID

internal class MobileSegmentForgeryFactory : ForgeryFactory<MobileSegment> {
    override fun getForgery(forge: Forge): MobileSegment {
        return MobileSegment(
            application = MobileSegment.Application(forge.getForgery<UUID>().toString()),
            session = MobileSegment.Session(forge.getForgery<UUID>().toString()),
            view = MobileSegment.View(forge.getForgery<UUID>().toString()),
            start = forge.aPositiveLong(),
            end = forge.aPositiveLong(),
            recordsCount = forge.aPositiveLong(),
            indexInView = forge.aNullable { forge.aPositiveLong() },
            hasFullSnapshot = forge.aNullable { forge.aBool() },
            source = forge.aValueFrom(MobileSegment.Source::class.java),
            // we only need this in the SessionReplayRequestFactory tests which doesn't
            // care about the records in the segment. We are not going to fake this data
            // as will require to duplicate all the factories from SR module.
            records = emptyList()
        )
    }
}
