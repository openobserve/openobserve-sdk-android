/*
 * Unless explicitly stated otherwise all files in this repository are licensed under the Apache License Version 2.0.
 * This product includes software developed at OpenObserve (https://www.openobserve.ai/).
 * Copyright 2016-Present OpenObserve, Inc.
 */

package com.openobserve.android.rx

import com.openobserve.android.OpenObserve
import com.openobserve.android.api.SdkCore
import com.openobserve.android.rum.GlobalRumMonitor
import com.openobserve.android.rum.RumErrorSource
import io.reactivex.rxjava3.functions.Consumer

/**
 * Provides an implementation of [Consumer<Throwable>] already set up to send relevant information
 * to OpenObserve.
 *
 * It will automatically send RUM error events whenever a RxJava Stream throws any [Exception].
 *
 * @param sdkCore the SDK instance to forward the errors to. If not provided, default instance
 * will be used.
 */
class OpenObserveRumErrorConsumer
@JvmOverloads
constructor(
    private val sdkCore: SdkCore = OpenObserve.getInstance()
) : Consumer<Throwable> {

    /** @inheritDoc */
    override fun accept(error: Throwable) {
        GlobalRumMonitor.get(sdkCore).addError(REQUEST_ERROR_MESSAGE, RumErrorSource.SOURCE, error)
    }

    internal companion object {
        internal const val REQUEST_ERROR_MESSAGE = "RxJava stream error"
    }
}
