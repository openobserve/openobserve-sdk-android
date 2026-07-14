/*
 * Unless explicitly stated otherwise all files in this repository are licensed under the Apache License Version 2.0.
 * This product includes software developed at OpenObserve (https://www.openobserve.ai/).
 * Copyright 2016-Present OpenObserve, Inc.
 */

package com.openobserve.android.sessionreplay.forge

import com.openobserve.android.sessionreplay.model.MobileSegment
import fr.xgouchet.elmyr.Forge
import fr.xgouchet.elmyr.ForgeryFactory

internal class MetaRecordForgeryFactory :
    ForgeryFactory<MobileSegment.MobileRecord.MetaRecord> {
    override fun getForgery(forge: Forge): MobileSegment.MobileRecord.MetaRecord {
        return MobileSegment.MobileRecord.MetaRecord(
            timestamp = forge.aPositiveLong(),
            data = MobileSegment.Data1(
                forge.aPositiveLong(),
                forge.aPositiveLong(),
                forge.aNullable { forge.aString() }
            ),
            slotId = forge.aNullable { aPositiveLong().toString() }
        )
    }
}
