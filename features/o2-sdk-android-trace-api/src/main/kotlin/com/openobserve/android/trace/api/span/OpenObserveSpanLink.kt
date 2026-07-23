/*
 * Unless explicitly stated otherwise all files in this repository are licensed under the Apache License Version 2.0.
 * This product includes software developed at Datadog (https://www.datadoghq.com/).
 * Copyright 2016-Present Datadog, Inc.
 */
package com.openobserve.android.trace.api.span

import com.openobserve.android.trace.api.trace.OpenObserveTraceId
import com.openobserve.tools.annotation.NoOpImplementation

/**
 * Represents a link to a OpenObserve span, which contains metadata and identifiers for associating a span
 * with its trace and related details.
 */
@NoOpImplementation
interface OpenObserveSpanLink {
    /**
     * The unique identifier for a OpenObserve span.
     */
    val spanId: Long

    /**
     * Indicates whether the OpenObserve span has been sampled.
     */
    val sampled: Boolean

    /**
     * The unique identifier for a OpenObserve trace.
     */
    val traceId: OpenObserveTraceId

    /**
     * Represents a string representation of a specific trace related to the OpenObserve span.
     */
    val traceStrace: String

    /**
     * A map containing key-value pairs of additional attributes associated with a OpenObserve span.
     */
    val attributes: Map<String, String>?
}
