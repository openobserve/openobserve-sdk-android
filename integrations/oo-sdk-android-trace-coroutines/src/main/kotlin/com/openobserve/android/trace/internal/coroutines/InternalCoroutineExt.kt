/*
 * Unless explicitly stated otherwise all files in this repository are licensed under the Apache License Version 2.0.
 * This product includes software developed at OpenObserve (https://www.openobserve.ai/).
 * Copyright 2016-Present OpenObserve, Inc.
 */

package com.openobserve.android.trace.internal.coroutines

import com.openobserve.android.trace.api.span.OpenObserveSpan
import com.openobserve.android.trace.coroutines.CoroutineScopeSpan
import com.openobserve.android.trace.withinSpan
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlin.coroutines.CoroutineContext

private const val TAG_DISPATCHER: String = "coroutine.dispatcher"

internal suspend fun <T : Any?> CoroutineScope.withinCoroutineSpan(
    operationName: String,
    parentSpan: OpenObserveSpan? = null,
    context: CoroutineContext,
    block: suspend CoroutineScopeSpan.() -> T
): T {
    return withinSpan(operationName, parentSpan, context != Dispatchers.Unconfined) {
        if (context is CoroutineDispatcher) {
            setTag(TAG_DISPATCHER, context.toString())
        }
        @Suppress("UnsafeThirdPartyFunctionCall") // handled by caller
        block(CoroutineScopeSpanImpl(this@withinCoroutineSpan, this))
    }
}
