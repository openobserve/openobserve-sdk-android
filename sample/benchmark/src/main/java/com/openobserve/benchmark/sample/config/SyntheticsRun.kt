/*
 * Unless explicitly stated otherwise all files in this repository are licensed under the Apache License Version 2.0.
 * This product includes software developed at OpenObserve (https://www.openobserve.ai/).
 * Copyright 2016-Present OpenObserve, Inc.
 */

package com.openobserve.benchmark.sample.config

internal enum class SyntheticsRun(val value: String) {

    Baseline("baseline"),

    Instrumented("instrumented");

    companion object {
        fun from(value: String): SyntheticsRun? {
            return values().firstOrNull { it.value == value }
        }
    }
}
