/*
 * Unless explicitly stated otherwise all files in this repository are licensed under the Apache License Version 2.0.
 * This product includes software developed at OpenObserve (https://www.openobserve.ai/).
 * Copyright 2016-Present OpenObserve, Inc.
 */
@file:Suppress("DEPRECATION")

package com.openobserve.android.okhttp.trace

import androidx.annotation.FloatRange
import com.openobserve.android.core.sampling.DeterministicSampler
import com.openobserve.android.okhttp.internal.utils.SpanSamplingIdProvider
import com.openobserve.android.trace.api.span.OpenObserveSpan

@Deprecated(
    "Use com.openobserve.android.trace.DeterministicTraceSampler instead.",
    replaceWith = ReplaceWith(
        "DeterministicTraceSampler",
        imports = ["com.openobserve.android.trace.DeterministicTraceSampler"]
    )
)
/**
 * Deprecated. Use [com.openobserve.android.trace.DeterministicTraceSampler] instead.
 *
 * A [DeterministicSampler] using the TraceID of a Span to compute the sampling decision.
 *
 * @param sampleRateProvider Provider for the sample rate value which will be called each time
 * the sampling decision needs to be made. All the values should be in the range [0;100].
 */
// TODO RUM-13454 Remove with SDK v4 release.
open class DeterministicTraceSampler(
    sampleRateProvider: () -> Float
) : DeterministicSampler<OpenObserveSpan>(
    SpanSamplingIdProvider::provideId,
    sampleRateProvider
) {

    /**
     * Creates a new instance lof [DeterministicSampler] with the given sample rate.
     *
     * @param sampleRate Sample rate to use.
     */
    constructor(
        @FloatRange(from = 0.0, to = 100.0) sampleRate: Float
    ) : this({ sampleRate })

    /**
     * Creates a new instance of [DeterministicSampler] with the given sample rate.
     *
     * @param sampleRate Sample rate to use.
     */
    constructor(
        @FloatRange(from = 0.0, to = 100.0) sampleRate: Double
    ) : this(sampleRate.toFloat())
}
