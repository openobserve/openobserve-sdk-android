/*
 * Unless explicitly stated otherwise all files in this repository are licensed under the Apache License Version 2.0.
 * This product includes software developed at OpenObserve (https://www.openobserve.ai/).
 * Copyright 2016-Present OpenObserve, Inc.
 */

package com.openobserve.android.okhttp.utils

import com.openobserve.android.rum.GlobalRumMonitor
import kotlin.reflect.full.memberFunctions
import kotlin.reflect.jvm.isAccessible

internal fun GlobalRumMonitor.reset() {
    this::class.memberFunctions
        .first { it.name == "reset" }
        .apply { this.isAccessible = true }
        .call(this::class.objectInstance)
}
