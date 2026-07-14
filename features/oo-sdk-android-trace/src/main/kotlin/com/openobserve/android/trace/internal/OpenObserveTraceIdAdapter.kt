/*
 * Unless explicitly stated otherwise all files in this repository are licensed under the Apache License Version 2.0.
 * This product includes software developed at OpenObserve (https://www.openobserve.ai/).
 * Copyright 2016-Present OpenObserve, Inc.
 */
package com.openobserve.android.trace.internal

import com.openobserve.android.trace.api.trace.OpenObserveTraceId
import com.openobserve.trace.api.DDTraceId

internal data class OpenObserveTraceIdAdapter(private val delegate: DDTraceId) : OpenObserveTraceId, DDTraceId() {
    override fun toLong(): Long = delegate.toLong()
    override fun toString(): String = delegate.toString()
    override fun toHexString(): String = delegate.toHexString()
    override fun toHighOrderLong(): Long = delegate.toHighOrderLong()
    override fun toHexStringPadded(size: Int): String = delegate.toHexStringPadded(size)
}
