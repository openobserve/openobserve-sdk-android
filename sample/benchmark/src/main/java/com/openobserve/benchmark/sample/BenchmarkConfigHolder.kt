/*
 * Unless explicitly stated otherwise all files in this repository are licensed under the Apache License Version 2.0.
 * This product includes software developed at OpenObserve (https://www.openobserve.ai/).
 * Copyright 2016-Present OpenObserve, Inc.
 */

package com.openobserve.benchmark.sample

import com.openobserve.benchmark.sample.config.BenchmarkConfig
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
internal class BenchmarkConfigHolder @Inject constructor() {
    lateinit var config: BenchmarkConfig
}
