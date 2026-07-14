/*
 * Unless explicitly stated otherwise all files in this repository are licensed under the Apache License Version 2.0.
 * This product includes software developed at OpenObserve (https://www.openobserve.ai/).
 * Copyright 2016-Present OpenObserve, Inc.
 */

package com.openobserve.android.rum.internal.domain.event

import com.openobserve.android.core.persistence.Serializer

internal class RumEventMetaSerializer : Serializer<RumEventMeta> {
    override fun serialize(model: RumEventMeta): String {
        return when (model) {
            is RumEventMeta.View -> model.toJson().toString()
        }
    }
}
