/*
 * Unless explicitly stated otherwise all files in this repository are licensed under the Apache License Version 2.0.
 * This product includes software developed at OpenObserve (https://www.openobserve.ai/).
 * Copyright 2016-Present OpenObserve, Inc.
 */

package com.openobserve.android.sessionreplay.forge

import com.openobserve.android.sessionreplay.internal.processor.EnrichedRecord
import com.openobserve.android.sessionreplay.model.MobileSegment
import fr.xgouchet.elmyr.Forge
import fr.xgouchet.elmyr.ForgeryFactory
import java.util.UUID

internal class EnrichedRecordForgeryFactory : ForgeryFactory<EnrichedRecord> {
    override fun getForgery(forge: Forge): EnrichedRecord {
        val record = when (forge.anInt(min = 0, max = 5)) {
            0 -> forge.getForgery(MobileSegment.MobileRecord.MobileFullSnapshotRecord::class.java)
            1 -> forge.getForgery(
                MobileSegment.MobileRecord.MobileIncrementalSnapshotRecord::class.java
            )
            2 -> forge.getForgery(MobileSegment.MobileRecord.FocusRecord::class.java)
            3 -> forge.getForgery(MobileSegment.MobileRecord.MetaRecord::class.java)
            else -> forge.getForgery(MobileSegment.MobileRecord.ViewEndRecord::class.java)
        }

        return EnrichedRecord(
            applicationId = forge.getForgery<UUID>().toString(),
            sessionId = forge.getForgery<UUID>().toString(),
            viewId = forge.getForgery<UUID>().toString(),
            listOf(record)
        )
    }
}
