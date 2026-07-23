/*
 * Unless explicitly stated otherwise all files in this repository are licensed under the Apache License Version 2.0.
 * This product includes software developed at Datadog (https://www.datadoghq.com/).
 * Copyright 2016-Present Datadog, Inc.
 */

package com.openobserve.android.rum.internal

import com.openobserve.android.api.context.OpenObserveContext
import com.openobserve.android.api.feature.Feature

internal class FeaturesContextResolver {

    @Suppress("UNCHECKED_CAST")
    fun resolveViewHasReplay(ooContext: OpenObserveContext, viewId: String): Boolean {
        val sessionReplayContext =
            ooContext.featuresContext[Feature.SESSION_REPLAY_FEATURE_NAME] ?: return false
        val sessionReplayMetadata = sessionReplayContext[viewId] as? Map<String, Any?>
        return (sessionReplayMetadata?.get(HAS_REPLAY_KEY) as? Boolean) ?: false
    }

    @Suppress("UNCHECKED_CAST")
    fun resolveViewRecordsCount(ooContext: OpenObserveContext, viewId: String): Long {
        val sessionReplayContext =
            ooContext.featuresContext[Feature.SESSION_REPLAY_FEATURE_NAME] ?: return 0L
        val sessionReplayMetadata = sessionReplayContext[viewId] as? Map<String, Any?>
        return (sessionReplayMetadata?.get(VIEW_RECORDS_COUNT_KEY) as? Long) ?: 0L
    }

    companion object {
        internal const val HAS_REPLAY_KEY = "has_replay"
        internal const val VIEW_RECORDS_COUNT_KEY = "records_count"
    }
}
