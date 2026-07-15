/*
 * Unless explicitly stated otherwise all files in this repository are licensed under the Apache License Version 2.0.
 * This product includes software developed at Datadog (https://www.datadoghq.com/).
 * Copyright 2016-Present Datadog, Inc.
 */

package com.openobserve.android.flags.internal.net

import androidx.annotation.WorkerThread
import com.openobserve.android.api.context.OpenObserveContext
import com.openobserve.android.flags.model.EvaluationContext
import com.openobserve.tools.annotation.NoOpImplementation

@NoOpImplementation
internal interface PrecomputedAssignmentsReader {
    @WorkerThread
    fun readPrecomputedFlags(context: EvaluationContext, ooContext: OpenObserveContext): String?
}
