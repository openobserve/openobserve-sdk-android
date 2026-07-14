/*
 * Unless explicitly stated otherwise all files in this repository are licensed under the Apache License Version 2.0.
 * This product includes software developed at OpenObserve (https://www.openobserve.ai/).
 * Copyright 2016-Present OpenObserve, Inc.
 */

package com.openobserve.android.sqldelight

import com.openobserve.android.trace.api.span.OpenObserveSpan
import com.squareup.sqldelight.TransactionWithoutReturn

/**
 * An object that implements both [OpenObserveSpan] and [TransactionWithoutReturn].
 */
interface TransactionWithSpanAndWithoutReturn : TransactionWithoutReturn, OpenObserveSpan
