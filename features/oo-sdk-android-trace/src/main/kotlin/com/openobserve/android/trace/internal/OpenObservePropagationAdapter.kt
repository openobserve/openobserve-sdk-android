/*
 * Unless explicitly stated otherwise all files in this repository are licensed under the Apache License Version 2.0.
 * This product includes software developed at Datadog (https://www.datadoghq.com/).
 * Copyright 2016-Present Datadog, Inc.
 */

package com.openobserve.android.trace.internal

import com.openobserve.android.api.InternalLogger
import com.openobserve.android.trace.api.propagation.OpenObservePropagation
import com.openobserve.android.trace.api.span.OpenObserveSpanContext
import com.openobserve.trace.bootstrap.instrumentation.api.AgentPropagation
import kotlin.reflect.KClass

internal class OpenObservePropagationAdapter(
    private val internalLogger: InternalLogger,
    private val delegate: AgentPropagation
) : OpenObservePropagation {

    override fun <C> inject(
        context: OpenObserveSpanContext,
        carrier: C,
        setter: (carrier: C, key: String, value: String) -> Unit
    ) {
        if (context !is OpenObserveSpanContextAdapter) {
            internalLogger.log(
                InternalLogger.Level.ERROR,
                InternalLogger.Target.USER,
                { constructErrorMessage(context::class) }
            )
            return
        }
        delegate.inject(context.delegate, carrier, setter)
    }

    override fun <C> extract(
        carrier: C,
        getter: (carrier: C, classifier: (String, String) -> Boolean) -> Unit
    ): OpenObserveSpanContext? {
        return delegate.extract(carrier) { car, cls -> getter(car, cls::accept) }
            ?.let { OpenObserveSpanContextAdapter(it) }
    }

    private fun constructErrorMessage(klass: KClass<*>) = "OpenObservePropagationAdapter supports only" +
        " OpenObserveSpanContextAdapter instances for injection but ${klass.simpleName} is given"
}
