/*
 * Unless explicitly stated otherwise all files in this repository are licensed under the Apache License Version 2.0.
 * This product includes software developed at OpenObserve (https://www.openobserve.ai/).
 * Copyright 2016-Present OpenObserve, Inc.
 */

package com.openobserve.android.sessionreplay.internal.resources

import com.openobserve.android.core.persistence.Serializer
import com.openobserve.android.sessionreplay.model.ResourceHashesEntry

internal class ResourceHashesEntrySerializer : Serializer<ResourceHashesEntry> {
    override fun serialize(model: ResourceHashesEntry): String {
        return model.toJson().toString()
    }
}
