/*
 * Unless explicitly stated otherwise all files in this repository are licensed under the Apache License Version 2.0.
 * This product includes software developed at OpenObserve (https://www.openobserve.ai/).
 * Copyright 2016-Present OpenObserve, Inc.
 */

package com.openobserve.android.flags.internal.model

import com.openobserve.android.flags.model.EvaluationContext

internal data class FlagsStateEntry(
    val evaluationContext: EvaluationContext,
    val flags: Map<String, PrecomputedFlag>,
    val lastUpdateTimestamp: Long
)
