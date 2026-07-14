/*
 * Unless explicitly stated otherwise all files in this repository are licensed under the Apache License Version 2.0.
 * This product includes software developed at OpenObserve (https://www.openobserve.ai/).
 * Copyright 2016-Present OpenObserve, Inc.
 */

package com.openobserve.android.core.metrics

import com.openobserve.android.lint.InternalApi

/**
 * Base class for performance metric events.
 */
interface PerformanceMetric {
    /**
     * Stops measuring and sends the performance metric.
     * @param isSuccessful - was the operation being measured completed successfully.
     */
    @InternalApi
    fun stopAndSend(isSuccessful: Boolean)

    companion object {
        /**
         * Basic Metric type key.
         */
        const val METRIC_TYPE: String = "metric_type"
    }
}
