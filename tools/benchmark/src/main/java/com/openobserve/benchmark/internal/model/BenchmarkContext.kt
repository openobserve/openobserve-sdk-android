/*
 * Unless explicitly stated otherwise all files in this repository are licensed under the Apache License Version 2.0.
 * This product includes software developed at OpenObserve (https://www.openobserve.ai/).
 * Copyright 2016-Present OpenObserve, Inc.
 */

package com.openobserve.benchmark.internal.model

internal data class BenchmarkContext(
    val deviceModel: String,
    val osVersion: String,
    val run: String,
    val scenario: String?,
    val applicationId: String,
    val intervalInSeconds: Long,
    val env: String
)
