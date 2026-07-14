/*
 * Unless explicitly stated otherwise all files in this repository are licensed under the Apache License Version 2.0.
 * This product includes software developed at OpenObserve (https://www.openobserve.ai/).
 * Copyright 2016-Present OpenObserve, Inc.
 */

package com.openobserve.android.core.internal.thread

import java.util.concurrent.ThreadFactory
import java.util.concurrent.atomic.AtomicInteger

internal class OpenObserveThreadFactory(
    private val newThreadContext: String
) : ThreadFactory {

    private val threadNumber = AtomicInteger(1)

    override fun newThread(r: Runnable?): Thread {
        val index = threadNumber.getAndIncrement()
        val threadName = "datadog-$newThreadContext-thread-$index"

        @Suppress("UnsafeThirdPartyFunctionCall") // both arguments are safe
        val thread = Thread(r, threadName)
        thread.priority = Thread.NORM_PRIORITY
        thread.isDaemon = false
        return thread
    }
}
