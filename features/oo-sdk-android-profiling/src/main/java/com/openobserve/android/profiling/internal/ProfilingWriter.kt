/*
 * Unless explicitly stated otherwise all files in this repository are licensed under the Apache License Version 2.0.
 * This product includes software developed at OpenObserve (https://www.openobserve.ai/).
 * Copyright 2016-Present OpenObserve, Inc.
 */

package com.openobserve.android.profiling.internal

import com.openobserve.android.internal.profiling.TTIDRumContext
import com.openobserve.android.profiling.internal.perfetto.PerfettoResult
import com.openobserve.tools.annotation.NoOpImplementation

@NoOpImplementation
internal interface ProfilingWriter {

    fun write(
        profilingResult: PerfettoResult,
        ttidRumContext: TTIDRumContext
    )
}
