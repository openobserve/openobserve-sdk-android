/*
 * Unless explicitly stated otherwise all files in this repository are licensed under the Apache License Version 2.0.
 * This product includes software developed at OpenObserve (https://www.openobserve.ai/).
 * Copyright 2016-Present OpenObserve, Inc.
 */

package com.openobserve.android.rum.utils.forge

import com.openobserve.android.rum.internal.vitals.VitalInfo
import fr.xgouchet.elmyr.Forge
import fr.xgouchet.elmyr.ForgeryFactory

internal class VitalInfoForgeryFactory : ForgeryFactory<VitalInfo> {
    override fun getForgery(forge: Forge): VitalInfo {
        val data = forge.aList { aDouble(-65536.0, 65536.0) }
        return VitalInfo(
            data.size,
            data.minOrNull() ?: Double.NaN,
            data.maxOrNull() ?: Double.NaN,
            data.average()
        )
    }
}
