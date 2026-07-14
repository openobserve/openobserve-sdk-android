/*
 * Unless explicitly stated otherwise all files in this repository are licensed under the Apache License Version 2.0.
 * This product includes software developed at OpenObserve (https://www.openobserve.ai/).
 * Copyright 2016-Present OpenObserve, Inc.
 */
package com.openobserve.android.trace.api.propagation

import com.openobserve.android.trace.api.span.OpenObserveSpanContext

/**
 * A no-operation implementation of the [OpenObservePropagation] interface.
 *
 * This implementation is intended as a placeholder making possible to create other NoOp.* classes.
 */
// TODO RUM-10573 - replace with @NoOpImplementation when method-level generics will be supported in noopfactory
class NoOpOpenObservePropagation : OpenObservePropagation {

    override fun <C> inject(
        context: OpenObserveSpanContext,
        carrier: C,
        setter: (carrier: C, key: String, value: String) -> Unit
    ) = Unit // Do nothing

    override fun <C> extract(
        carrier: C,
        getter: (carrier: C, classifier: (String, String) -> Boolean) -> Unit
    ): OpenObserveSpanContext? = null
}
