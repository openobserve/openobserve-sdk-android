/*
 * Unless explicitly stated otherwise all files in this repository are licensed under the Apache License Version 2.0.
 * This product includes software developed at OpenObserve (https://www.openobserve.ai/).
 * Copyright 2016-Present OpenObserve, Inc.
 */

package com.openobserve.android.internal.profiler

import com.openobserve.tools.annotation.NoOpImplementation

/**
 * Interface of benchmark span to be implemented. This should only be used by internal benchmarking.
 */
@NoOpImplementation
interface BenchmarkSpan {

    /**
     * Marks the end of [BenchmarkSpan] execution.
     */
    fun stop()
}
