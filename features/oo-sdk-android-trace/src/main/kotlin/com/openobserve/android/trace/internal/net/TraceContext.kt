/*
 * Unless explicitly stated otherwise all files in this repository are licensed under the Apache License Version 2.0.
 * This product includes software developed at OpenObserve (https://www.openobserve.ai/).
 * Copyright 2016-Present OpenObserve, Inc.
 */

package com.openobserve.android.trace.internal.net

import com.openobserve.android.lint.InternalApi

/**
 * The context of a trace to be propagated through HTTP requests for OpenObserve tracing.
 */
@InternalApi
// TODO RUM-13441: this class should be internal after refactor of TracingInterceptor.
data class TraceContext(
    /**
     * The trace id.
     */
    val traceId: String,
    /**
     * The span id.
     */
    val spanId: String,
    /**
     * The sampling priority.
     */
    val samplingPriority: Int
)
