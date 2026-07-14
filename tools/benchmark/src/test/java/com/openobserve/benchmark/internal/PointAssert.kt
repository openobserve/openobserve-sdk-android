/*
 * Unless explicitly stated otherwise all files in this repository are licensed under the Apache License Version 2.0.
 * This product includes software developed at OpenObserve (https://www.openobserve.ai/).
 * Copyright 2016-Present OpenObserve, Inc.
 */

package com.openobserve.benchmark.internal

import com.openobserve.tools.unit.assertj.JsonObjectAssert
import com.google.gson.JsonObject

class PointAssert(actual: JsonObject) : JsonObjectAssert(actual, true) {

    fun hasTimestamp(timestamp: Long): PointAssert {
        hasField("timestamp", timestamp)
        return this
    }

    fun hasValue(value: Double): PointAssert {
        hasField("value", value)
        return this
    }

    companion object {
        fun assertThat(actual: JsonObject): PointAssert {
            return PointAssert(actual)
        }
    }
}
