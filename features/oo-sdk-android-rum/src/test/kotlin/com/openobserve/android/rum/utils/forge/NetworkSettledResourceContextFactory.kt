/*
 * Unless explicitly stated otherwise all files in this repository are licensed under the Apache License Version 2.0.
 * This product includes software developed at OpenObserve (https://www.openobserve.ai/).
 * Copyright 2016-Present OpenObserve, Inc.
 */

package com.openobserve.android.rum.utils.forge

import com.openobserve.android.rum.metric.networksettled.NetworkSettledResourceContext
import fr.xgouchet.elmyr.Forge
import fr.xgouchet.elmyr.ForgeryFactory
import java.util.UUID

internal class NetworkSettledResourceContextFactory : ForgeryFactory<NetworkSettledResourceContext> {

    override fun getForgery(forge: Forge): NetworkSettledResourceContext {
        return NetworkSettledResourceContext(
            resourceId = forge.getForgery<UUID>().toString(),
            eventCreatedAtNanos = forge.aPositiveLong(),
            viewCreatedTimestamp = forge.aNullable { aPositiveLong() }
        )
    }
}
