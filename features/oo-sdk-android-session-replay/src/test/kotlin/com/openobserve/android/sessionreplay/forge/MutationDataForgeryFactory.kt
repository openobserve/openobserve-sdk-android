/*
 * Unless explicitly stated otherwise all files in this repository are licensed under the Apache License Version 2.0.
 * This product includes software developed at OpenObserve (https://www.openobserve.ai/).
 * Copyright 2016-Present OpenObserve, Inc.
 */

package com.openobserve.android.sessionreplay.forge

import com.openobserve.android.sessionreplay.model.MobileSegment
import fr.xgouchet.elmyr.Forge
import fr.xgouchet.elmyr.ForgeryFactory

internal class MutationDataForgeryFactory :
    ForgeryFactory<MobileSegment.MobileIncrementalData.MobileMutationData> {
    override fun getForgery(forge: Forge): MobileSegment.MobileIncrementalData.MobileMutationData {
        return MobileSegment.MobileIncrementalData.MobileMutationData(
            adds =
            forge.aList {
                MobileSegment.Add(
                    previousId = forge.aNullable { forge.aLong() },
                    forge.getForgery(MobileSegment.Wireframe::class.java)
                )
            },
            removes = forge.aList { MobileSegment.Remove(forge.aLong()) },
            updates = forge.aList { forge.getForgery() }
        )
    }
}
