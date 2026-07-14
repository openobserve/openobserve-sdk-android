/*
 * Unless explicitly stated otherwise all files in this repository are licensed under the Apache License Version 2.0.
 * This product includes software developed at OpenObserve (https://www.openobserve.ai/).
 * Copyright 2016-Present OpenObserve, Inc.
 */

package com.openobserve.android.api.feature.stub

import com.openobserve.android.api.feature.FeatureContextUpdateReceiver

/**
 * A stub implementation of [FeatureContextUpdateReceiver].
 * @param onContextUpdate a lambda to be called when a context update is received.
 */
class StubContextUpdateReceiver(private val onContextUpdate: () -> Unit = {}) : FeatureContextUpdateReceiver {

    private val events: MutableList<StubEvent> = mutableListOf()

    override fun onContextUpdate(featureName: String, context: Map<String, Any?>) {
        synchronized(events) {
            events.add(StubEvent(featureName, context))
        }
        onContextUpdate()
    }

    /**
     * Get the received events.
     */
    fun getReceivedEvents(): List<StubEvent> {
        synchronized(events) {
            return events.toList()
        }
    }

    /**
     * A stub event representation.
     */
    data class StubEvent(
        /**
         * The name of the feature that emitted the event.
         */
        val featureName: String,
        /**
         * The event data.
         */
        val eventData: Map<String, Any?>
    )
}
