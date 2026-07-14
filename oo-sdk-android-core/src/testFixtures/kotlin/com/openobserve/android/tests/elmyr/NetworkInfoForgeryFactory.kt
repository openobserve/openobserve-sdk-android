/*
 * Unless explicitly stated otherwise all files in this repository are licensed under the Apache License Version 2.0.
 * This product includes software developed at OpenObserve (https://www.openobserve.ai/).
 * Copyright 2016-Present OpenObserve, Inc.
 */

package com.openobserve.android.tests.elmyr

import com.openobserve.android.api.context.NetworkInfo
import fr.xgouchet.elmyr.Forge
import fr.xgouchet.elmyr.ForgeryFactory

class NetworkInfoForgeryFactory : ForgeryFactory<NetworkInfo> {

    @Suppress("MagicNumber")
    override fun getForgery(forge: Forge): NetworkInfo {
        return NetworkInfo(
            connectivity = forge.aValueFrom(NetworkInfo.Connectivity::class.java),
            carrierName = forge.anElementFrom(
                forge.anAlphabeticalString(),
                forge.aWhitespaceString(),
                null
            ),
            carrierId = forge.aNullable { forge.aLong(0, 10000) },
            upKbps = forge.aNullable { forge.aLong(1, Long.MAX_VALUE) },
            downKbps = forge.aNullable { forge.aLong(1, Long.MAX_VALUE) },
            strength = forge.aNullable { forge.aLong(-100, -30) }, // dBm for Wi-Fi signal
            cellularTechnology = forge.aNullable { anAlphabeticalString() }
        )
    }
}
