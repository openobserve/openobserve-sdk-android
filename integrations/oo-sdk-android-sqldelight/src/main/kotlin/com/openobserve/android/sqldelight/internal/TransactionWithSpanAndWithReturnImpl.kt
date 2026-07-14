/*
 * Unless explicitly stated otherwise all files in this repository are licensed under the Apache License Version 2.0.
 * This product includes software developed at OpenObserve (https://www.openobserve.ai/).
 * Copyright 2016-Present OpenObserve, Inc.
 */

package com.openobserve.android.sqldelight.internal

import com.openobserve.android.sqldelight.TransactionWithSpanAndWithReturn
import com.openobserve.android.trace.api.span.OpenObserveSpan
import com.squareup.sqldelight.TransactionWithReturn

internal class TransactionWithSpanAndWithReturnImpl<R>(
    private val span: OpenObserveSpan,
    private val transaction: TransactionWithReturn<R>
) : TransactionWithSpanAndWithReturn<R>, OpenObserveSpan by span, TransactionWithReturn<R> by transaction
