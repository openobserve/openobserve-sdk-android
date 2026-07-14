/*
 * Unless explicitly stated otherwise all files in this repository are licensed under the Apache License Version 2.0.
 * This product includes software developed at OpenObserve (https://www.openobserve.ai/).
 * Copyright 2016-Present OpenObserve, Inc.
 */

package com.openobserve.android.trace.internal.domain.event

import com.openobserve.android.api.InternalLogger
import com.openobserve.android.event.EventMapper
import com.openobserve.android.trace.event.SpanEventMapper
import com.openobserve.android.trace.model.SpanEvent
import java.util.Locale

internal class SpanEventMapperWrapper(
    internal val wrappedEventMapper: SpanEventMapper,
    private val internalLogger: InternalLogger
) : EventMapper<SpanEvent> {
    override fun map(event: SpanEvent): SpanEvent? {
        val mappedEvent = wrappedEventMapper.map(event)
        if (mappedEvent !== event) {
            internalLogger.log(
                InternalLogger.Level.INFO,
                InternalLogger.Target.USER,
                { NOT_SAME_EVENT_INSTANCE_WARNING_MESSAGE.format(Locale.US, event) }
            )
            return null
        }
        return mappedEvent
    }

    companion object {
        internal const val NOT_SAME_EVENT_INSTANCE_WARNING_MESSAGE =
            "SpanEventMapper: the returned mapped object was not the " +
                "same instance as the original object. This event will be dropped: %s"
    }
}
