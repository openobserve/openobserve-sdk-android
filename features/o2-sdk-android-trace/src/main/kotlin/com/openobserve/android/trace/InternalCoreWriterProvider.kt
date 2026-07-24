/*
 * Unless explicitly stated otherwise all files in this repository are licensed under the Apache License Version 2.0.
 * This product includes software developed at Datadog (https://www.datadoghq.com/).
 * Copyright 2016-Present Datadog, Inc.
 */

package com.openobserve.android.trace

import com.openobserve.android.lint.InternalApi
import com.openobserve.android.trace.api.span.OpenObserveSpanWriter

/**
 * Internal interface to provide the core writer to the tracer.
 * The [com.openobserve.trace.core.CoreTracer] uses this interface to be able to provide custom
 * implementations of the writer. By using this interface, the tracer can be decoupled from the
 * o2-sdk-android-trace-otel module by providing the writer implementation from the o2-sdk-android-trace module.
 * The reason we cannot provide the implementation directly in the o2-track-android-trace-otel module is that the
 * current Writer implementation is depending on too many classes from the o2-sdk-android-trace module.
 */
@InternalApi
interface InternalCoreWriterProvider {

    /**
     * Returns the core writer used by the tracer.
     */
    fun getCoreTracerWriter(): OpenObserveSpanWriter
}
