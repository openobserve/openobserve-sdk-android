/*
 * Unless explicitly stated otherwise all files in this repository are licensed under the Apache License Version 2.0.
 * This product includes software developed at OpenObserve (https://www.openobserve.ai/).
 * Copyright 2016-Present OpenObserve, Inc.
 */

package com.openobserve.android.rum.utils.forge

import com.openobserve.android.rum.internal.domain.event.RumEventMeta
import fr.xgouchet.elmyr.Forge
import fr.xgouchet.elmyr.ForgeryFactory
import java.util.UUID

internal class ViewEventMetaForgeryFactory : ForgeryFactory<RumEventMeta.View> {
    override fun getForgery(forge: Forge): RumEventMeta.View {
        return RumEventMeta.View(
            viewId = forge.getForgery<UUID>().toString(),
            documentVersion = forge.aPositiveLong(strict = true),
            hasAccessibility = forge.aBool()
        )
    }
}
