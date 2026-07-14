/*
 * Unless explicitly stated otherwise all files in this repository are licensed under the Apache License Version 2.0.
 * This product includes software developed at OpenObserve (https://www.openobserve.ai/).
 * Copyright 2016-Present OpenObserve, Inc.
 */

package com.openobserve.android.flags.internal.storage

import com.openobserve.android.api.feature.Feature
import com.openobserve.android.api.feature.FeatureSdkCore
import com.openobserve.android.api.storage.EventType
import com.openobserve.android.api.storage.RawBatchEvent
import com.openobserve.android.flags.internal.EvaluationEventWriter
import com.openobserve.android.flags.internal.aggregation.EvaluationAggregationStats

/**
 * Persists serialized flag evaluation events to SDK Core storage.
 *
 * Events are written to the EVALUATIONS feature storage and will be uploaded
 * to the EVP intake endpoint by the SDK Core's upload mechanism.
 */
internal class EvaluationEventRecordWriter(private val sdkCore: FeatureSdkCore) : EvaluationEventWriter {
    override fun writeAll(events: List<EvaluationAggregationStats>) {
        if (events.isEmpty()) return

        sdkCore.getFeature(Feature.FLAGS_EVALUATIONS_FEATURE_NAME)
            ?.withWriteContext { ooContext, writeScope ->
                writeScope { batchWriter ->
                    for (event in events) {
                        val serializedRecord = event.toEvaluationEvent(ooContext)
                            .toJson()
                            .toString()
                            .toByteArray(Charsets.UTF_8)
                        val rawBatchEvent = RawBatchEvent(data = serializedRecord)
                        batchWriter.write(
                            event = rawBatchEvent,
                            batchMetadata = null,
                            eventType = EventType.DEFAULT
                        )
                    }
                }
            }
    }
}
