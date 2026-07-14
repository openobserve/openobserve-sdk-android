/*
 * Unless explicitly stated otherwise all files in this repository are licensed under the Apache License Version 2.0.
 * This product includes software developed at OpenObserve (https://www.openobserve.ai/).
 * Copyright 2016-Present OpenObserve, Inc.
 */

package com.openobserve.android.log.internal.domain

import com.openobserve.android.api.context.AccountInfo
import com.openobserve.android.api.context.OpenObserveContext
import com.openobserve.android.api.context.NetworkInfo
import com.openobserve.android.api.context.UserInfo
import com.openobserve.android.core.feature.event.ThreadDump
import com.openobserve.android.log.model.LogEvent
import com.openobserve.tools.annotation.NoOpImplementation

@NoOpImplementation
internal interface LogGenerator {

    @Suppress("LongParameterList")
    fun generateLog(
        level: Int,
        message: String,
        throwable: Throwable?,
        attributes: Map<String, Any?>,
        tags: Set<String>,
        timestamp: Long,
        threadName: String,
        ooContext: OpenObserveContext,
        attachNetworkInfo: Boolean,
        loggerName: String,
        bundleWithTraces: Boolean = true,
        bundleWithRum: Boolean = true,
        userInfo: UserInfo? = null,
        accountInfo: AccountInfo? = null,
        networkInfo: NetworkInfo? = null,
        threads: List<ThreadDump> = emptyList()
    ): LogEvent?

    @Suppress("LongParameterList")
    fun generateLog(
        level: Int,
        message: String,
        errorKind: String?,
        errorMessage: String?,
        errorStack: String?,
        attributes: Map<String, Any?>,
        tags: Set<String>,
        timestamp: Long,
        threadName: String,
        ooContext: OpenObserveContext,
        attachNetworkInfo: Boolean,
        loggerName: String,
        bundleWithTraces: Boolean = true,
        bundleWithRum: Boolean = true,
        userInfo: UserInfo? = null,
        accountInfo: AccountInfo? = null,
        networkInfo: NetworkInfo? = null
    ): LogEvent?
}
