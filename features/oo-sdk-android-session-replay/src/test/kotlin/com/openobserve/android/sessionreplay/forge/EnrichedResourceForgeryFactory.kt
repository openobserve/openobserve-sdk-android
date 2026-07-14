/*
 * Unless explicitly stated otherwise all files in this repository are licensed under the Apache License Version 2.0.
 * This product includes software developed at OpenObserve (https://www.openobserve.ai/).
 * Copyright 2016-Present OpenObserve, Inc.
 */

package com.openobserve.android.sessionreplay.forge

import com.openobserve.android.sessionreplay.internal.processor.EnrichedResource
import fr.xgouchet.elmyr.Forge
import fr.xgouchet.elmyr.ForgeryFactory
import java.util.UUID

internal class EnrichedResourceForgeryFactory : ForgeryFactory<EnrichedResource> {
    override fun getForgery(forge: Forge): EnrichedResource {
        return EnrichedResource(
            filename = forge.getForgery<UUID>().toString(),
            resource = forge.getForgery<UUID>().toString().toByteArray()
        )
    }
}
