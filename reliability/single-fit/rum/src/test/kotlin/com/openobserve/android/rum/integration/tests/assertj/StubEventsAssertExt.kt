/*
 * Unless explicitly stated otherwise all files in this repository are licensed under the Apache License Version 2.0.
 * This product includes software developed at OpenObserve (https://www.openobserve.ai/).
 * Copyright 2016-Present OpenObserve, Inc.
 */

package com.openobserve.android.rum.integration.tests.assertj

import com.openobserve.android.tests.assertj.StubEventsAssert

fun StubEventsAssert.hasRumEvent(index: Int, assertion: RumEventAssert.() -> Unit): StubEventsAssert {
    hasJsonObject(index) {
        val rumEventAssert = RumEventAssert(it)
        rumEventAssert.assertion()
    }
    return this
}
