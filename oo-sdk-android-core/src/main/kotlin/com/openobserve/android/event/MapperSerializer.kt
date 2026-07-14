/*
 * Unless explicitly stated otherwise all files in this repository are licensed under the Apache License Version 2.0.
 * This product includes software developed at OpenObserve (https://www.openobserve.ai/).
 * Copyright 2016-Present OpenObserve, Inc.
 */

package com.openobserve.android.event

import com.openobserve.android.core.persistence.Serializer

/**
 * Combines [EventMapper] and [Serializer]. First mapping is done, then serialization.
 *
 * @param T type of the data to map and serialize.
 * @param eventMapper Event mapper to use.
 * @param serializer Serializer to use.
 */
class MapperSerializer<T : Any>(
    private val eventMapper: EventMapper<T>,
    private val serializer: Serializer<T>
) : Serializer<T> {

    /** @inheritdoc */
    override fun serialize(model: T): String? {
        val mappedEvent = eventMapper.map(model) ?: return null
        return serializer.serialize(mappedEvent)
    }
}
