/*
 * Unless explicitly stated otherwise all files in this repository are licensed under the Apache License Version 2.0.
 * This product includes software developed at Datadog (https://www.datadoghq.com/).
 * Copyright 2016-Present Datadog, Inc.
 */
package com.openobserve.android.trace

import com.openobserve.android.trace.api.tracer.OpenObserveTracer
import com.openobserve.android.trace.api.tracer.NoOpOpenObserveTracer
import java.util.concurrent.atomic.AtomicReference

/**
 * A holder object for managing and retrieving a global instance of the [OpenObserveTracer].
 *
 * This object is used to share same instance of [OpenObserveTracer] across different integrations such as
 * `OkHttp`, Kotlin's coroutines, ect.
 */
object GlobalOpenObserveTracer {

    private val instance = AtomicReference<OpenObserveTracer?>()

    /**
     * Registers the provided tracer as the global tracer if no tracer is currently registered.
     *
     * @param tracer The tracer to register as the global tracer.
     * @return `true` if the tracer was successfully registered, or `false` if a tracer was already registered.
     */
    @JvmStatic
    fun registerIfAbsent(tracer: OpenObserveTracer): Boolean {
        return instance.compareAndSet(null, tracer)
    }

    /**
     * Retrieves the current active tracer for OpenObserve, or a no-operation tracer if none is active.
     *
     * @return The current instance of [OpenObserveTracer] if available. Otherwise, an instance of
     * [NoOpOpenObserveTracer] that performs no operations.
     */
    @JvmStatic
    fun get(): OpenObserveTracer = getOrNull() ?: NoOpOpenObserveTracer()

    /**
     * Retrieves the current instance of the OpenObserveTracer, if available.
     *
     * @return An instance of [OpenObserveTracer] or null.
     */
    fun getOrNull(): OpenObserveTracer? = instance.get()

    /**
     * Clears the current instance of the global OpenObserve tracer.
     *
     * This method sets the internal tracer instance to null, effectively
     * removing any active tracer currently held in the global state.
     * The general purpose is to use it for test implementation.
     */
    fun clear() {
        instance.set(null)
    }
}
