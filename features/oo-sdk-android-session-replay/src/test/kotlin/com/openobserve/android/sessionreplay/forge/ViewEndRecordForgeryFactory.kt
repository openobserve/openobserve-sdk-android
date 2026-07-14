/*
 * Unless explicitly stated otherwise all files in this repository are licensed under the Apache License Version 2.0.
 * This product includes software developed at OpenObserve (https://www.openobserve.ai/).
 * Copyright 2016-Present OpenObserve, Inc.
 */

package com.openobserve.android.sessionreplay.forge

import com.openobserve.android.sessionreplay.model.MobileSegment
import fr.xgouchet.elmyr.Forge
import fr.xgouchet.elmyr.ForgeryFactory

internal class ViewEndRecordForgeryFactory :
    ForgeryFactory<MobileSegment.MobileRecord.ViewEndRecord> {
    override fun getForgery(forge: Forge): MobileSegment.MobileRecord.ViewEndRecord {
        return MobileSegment.MobileRecord.ViewEndRecord(
            forge.aPositiveLong()
        )
    }
}
