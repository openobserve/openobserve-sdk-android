/*
 * Unless explicitly stated otherwise all files in this repository are licensed under the Apache License Version 2.0.
 * This product includes software developed at OpenObserve (https://www.openobserve.ai/).
 * Copyright 2016-Present OpenObserve, Inc.
 */

package com.openobserve.android.okhttp

import com.openobserve.android.lint.InternalApi

/**
 * Deprecated. Use com.openobserve.android.trace.internal.net.TraceContext instead."
 * The context of a trace to be propagated through the OkHttp requests for OpenObserve tracing.
 */
@InternalApi
@Deprecated(
    "Use com.openobserve.android.trace.internal.net.TraceContext instead.",
    replaceWith = ReplaceWith(
        "TraceContext",
        imports = ["com.openobserve.android.trace.internal.net.TraceContext"]
    )
)
// TODO RUM-13454 Remove with SDK v4 release.
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
