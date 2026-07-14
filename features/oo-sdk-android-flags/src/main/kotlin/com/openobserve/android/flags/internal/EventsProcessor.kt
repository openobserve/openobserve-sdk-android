/*
 * Unless explicitly stated otherwise all files in this repository are licensed under the Apache License Version 2.0.
 * This product includes software developed at OpenObserve (https://www.openobserve.ai/).
 * Copyright 2016-Present OpenObserve, Inc.
 */

package com.openobserve.android.flags.internal

import com.openobserve.android.flags.model.EvaluationContext
import com.openobserve.android.flags.model.UnparsedFlag
import com.openobserve.tools.annotation.NoOpImplementation

@NoOpImplementation
internal interface EventsProcessor {
    fun processEvent(flagName: String, context: EvaluationContext, data: UnparsedFlag)
}
