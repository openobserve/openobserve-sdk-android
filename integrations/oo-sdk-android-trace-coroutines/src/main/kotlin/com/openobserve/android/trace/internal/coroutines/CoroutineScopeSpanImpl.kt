/*
 * Unless explicitly stated otherwise all files in this repository are licensed under the Apache License Version 2.0.
 * This product includes software developed at OpenObserve (https://www.openobserve.ai/).
 * Copyright 2016-Present OpenObserve, Inc.
 */

package com.openobserve.android.trace.internal.coroutines

import com.openobserve.android.trace.api.span.OpenObserveSpan
import com.openobserve.android.trace.coroutines.CoroutineScopeSpan
import kotlinx.coroutines.CoroutineScope

internal class CoroutineScopeSpanImpl(
    private val scope: CoroutineScope,
    private val span: OpenObserveSpan
) : CoroutineScopeSpan,
    CoroutineScope by scope,
    OpenObserveSpan by span
