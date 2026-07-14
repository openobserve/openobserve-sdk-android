/*
 * Unless explicitly stated otherwise all files in this repository are licensed under the Apache License Version 2.0.
 * This product includes software developed at OpenObserve (https://www.openobserve.ai/).
 * Copyright 2016-Present OpenObserve, Inc.
 */

package com.openobserve.android.sessionreplay.internal

import com.openobserve.android.api.feature.Feature
import com.openobserve.android.api.feature.FeatureSdkCore
import com.openobserve.android.sessionreplay.internal.processor.EnrichedRecord

internal class SessionReplayRecordCallback(
    private val featureSdkCore: FeatureSdkCore
) : RecordCallback {

    @Suppress("UNCHECKED_CAST")
    override fun onRecordForViewSent(record: EnrichedRecord) {
        val recordsSize = record.records.size
        if (recordsSize > 0) {
            // we are already past the event processing pipeline and this method is called from write stage, we can
            // update directly from this thread, given we patially mutate context.
            featureSdkCore.updateFeatureContext(Feature.SESSION_REPLAY_FEATURE_NAME, useContextThread = false) {
                val viewId = record.viewId
                val viewMetadata = (it[viewId] as? MutableMap<String, Any?>) ?: mutableMapOf()
                viewMetadata[HAS_REPLAY_KEY] = true
                updateRecordsCount(viewMetadata, recordsSize)
                it[viewId] = viewMetadata
            }
        }
    }

    private fun updateRecordsCount(
        viewMetadata: MutableMap<String, Any?>,
        recordsCount: Int
    ) {
        val currentRecords = viewMetadata[VIEW_RECORDS_COUNT_KEY] as? Long ?: 0
        val newRecords = currentRecords + recordsCount
        viewMetadata[VIEW_RECORDS_COUNT_KEY] = newRecords
    }

    companion object {
        internal const val HAS_REPLAY_KEY = "has_replay"
        internal const val VIEW_RECORDS_COUNT_KEY = "records_count"
    }
}
