/*
 * Unless explicitly stated otherwise all files in this repository are licensed under the Apache License Version 2.0.
 * This product includes software developed at OpenObserve (https://www.openobserve.ai/).
 * Copyright 2016-Present OpenObserve, Inc.
 */

package com.openobserve.android.rum.utils.forge

import com.openobserve.android.rum.internal.metric.networksettled.InternalResourceContext
import fr.xgouchet.elmyr.Forge
import fr.xgouchet.elmyr.ForgeryFactory
import java.util.UUID

internal class InternalResourceContextFactory : ForgeryFactory<InternalResourceContext> {

    override fun getForgery(forge: Forge): InternalResourceContext {
        return InternalResourceContext(
            resourceId = forge.getForgery<UUID>().toString(),
            eventCreatedAtNanos = forge.aPositiveLong()
        )
    }
}
