/*
 * Unless explicitly stated otherwise all files in this repository are licensed under the Apache License Version 2.0.
 * This product includes software developed at OpenObserve (https://www.openobserve.ai/).
 * Copyright 2016-Present OpenObserve, Inc.
 */

package com.openobserve.android.sample.data

internal sealed class Result {
    class Success<out T>(val data: T) : Result()
    class Failure(val message: String? = null, val throwable: Throwable? = null) : Result()
}
