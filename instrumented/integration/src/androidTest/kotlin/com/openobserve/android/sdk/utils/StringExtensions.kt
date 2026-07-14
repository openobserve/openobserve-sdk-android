/*
 * Unless explicitly stated otherwise all files in this repository are licensed under the Apache License Version 2.0.
 * This product includes software developed at OpenObserve (https://www.openobserve.ai/).
 * Copyright 2016-Present OpenObserve, Inc.
 */

package com.openobserve.android.sdk.utils

internal fun String.isTracesUrl(): Boolean {
    return this.matches(Regex("(.*)/traces(.*)"))
}

internal fun String.isLogsUrl(): Boolean {
    return this.matches(Regex("(.*)/logs(.*)"))
}

internal fun String.isRumUrl(): Boolean {
    return this.matches(Regex("(.*)/rum(.*)"))
}
