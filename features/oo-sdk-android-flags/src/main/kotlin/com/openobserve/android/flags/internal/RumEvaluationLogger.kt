/*
 * Unless explicitly stated otherwise all files in this repository are licensed under the Apache License Version 2.0.
 * This product includes software developed at OpenObserve (https://www.openobserve.ai/).
 * Copyright 2016-Present OpenObserve, Inc.
 */

package com.openobserve.android.flags.internal

import com.openobserve.android.api.feature.FeatureScope
import com.openobserve.android.internal.flags.RumFlagEvaluationMessage
import com.openobserve.tools.annotation.NoOpImplementation

@NoOpImplementation
internal interface RumEvaluationLogger {
    fun logEvaluation(flagKey: String, value: Any)
}

internal class DefaultRumEvaluationLogger(private val featureScope: FeatureScope) : RumEvaluationLogger {
    override fun logEvaluation(flagKey: String, value: Any) {
        featureScope.sendEvent(
            RumFlagEvaluationMessage(
                flagKey = flagKey,
                value = value
            )
        )
    }
}
