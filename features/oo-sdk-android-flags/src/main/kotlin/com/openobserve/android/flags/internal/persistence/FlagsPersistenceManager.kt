/*
 * Unless explicitly stated otherwise all files in this repository are licensed under the Apache License Version 2.0.
 * This product includes software developed at OpenObserve (https://www.openobserve.ai/).
 * Copyright 2016-Present OpenObserve, Inc.
 */

package com.openobserve.android.flags.internal.persistence

import com.openobserve.android.api.InternalLogger
import com.openobserve.android.api.storage.datastore.DataStoreHandler
import com.openobserve.android.api.storage.datastore.DataStoreReadCallback
import com.openobserve.android.api.storage.datastore.DataStoreWriteCallback
import com.openobserve.android.core.persistence.datastore.DataStoreContent
import com.openobserve.android.flags.internal.model.FlagsStateEntry
import com.openobserve.android.flags.internal.model.PrecomputedFlag
import com.openobserve.android.flags.model.EvaluationContext

internal class FlagsPersistenceManager(
    private val dataStore: DataStoreHandler,
    instanceName: String,
    private val internalLogger: InternalLogger,
    onStateLoaded: (FlagsStateEntry?) -> Unit
) {
    private val serializer = FlagsStateSerializer(internalLogger)
    private val deserializer = FlagsStateDeserializer(internalLogger)
    private val flagsStateKey: String = "$FLAGS_STATE_KEY_PREFIX-$instanceName"

    init {
        loadFlagsState(onStateLoaded)
    }

    internal fun saveFlagsState(
        context: EvaluationContext,
        flags: Map<String, PrecomputedFlag>,
        currentTimestamp: Long,
        callback: DataStoreWriteCallback? = null
    ) {
        val entry = FlagsStateEntry(
            evaluationContext = context,
            flags = flags,
            lastUpdateTimestamp = currentTimestamp
        )

        dataStore.setValue(
            key = flagsStateKey,
            data = entry,
            serializer = serializer,
            callback = callback
        )
    }

    private fun loadFlagsState(onStateLoaded: (FlagsStateEntry?) -> Unit) {
        dataStore.value(
            key = flagsStateKey,
            deserializer = deserializer,
            callback = object : DataStoreReadCallback<FlagsStateEntry> {
                override fun onSuccess(dataStoreContent: DataStoreContent<FlagsStateEntry>?) {
                    val loadedState = dataStoreContent?.data
                    onStateLoaded(loadedState)
                }

                override fun onFailure() {
                    internalLogger.log(
                        InternalLogger.Level.WARN,
                        InternalLogger.Target.MAINTAINER,
                        { "No persisted flags state found or failed to load" }
                    )
                    onStateLoaded(null)
                }
            }
        )
    }

    companion object {
        private const val FLAGS_STATE_KEY_PREFIX = "flags-state"
    }
}
