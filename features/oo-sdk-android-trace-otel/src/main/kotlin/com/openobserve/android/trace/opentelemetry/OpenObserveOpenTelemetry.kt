/*
 * Unless explicitly stated otherwise all files in this repository are licensed under the Apache License Version 2.0.
 * This product includes software developed at OpenObserve (https://www.openobserve.ai/).
 * Copyright 2016-Present OpenObserve, Inc.
 */
package com.openobserve.android.trace.opentelemetry

import io.opentelemetry.api.OpenTelemetry
import io.opentelemetry.api.trace.TracerProvider
import io.opentelemetry.context.propagation.ContextPropagators

/**
 * Default implementation of the OpenTelemetry interface that integrates with OpenObserve.
 */
class OpenObserveOpenTelemetry(serviceName: String) : OpenTelemetry {

    private val tracerProvider = OtelTracerProvider.Builder()
        .setService(serviceName)
        .build()

    override fun getTracerProvider(): TracerProvider = tracerProvider

    override fun getPropagators(): ContextPropagators = ContextPropagators.noop()
}
