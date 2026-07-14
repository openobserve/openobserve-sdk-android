/*
 * Unless explicitly stated otherwise all files in this repository are licensed under the Apache License Version 2.0.
 * This product includes software developed at OpenObserve (https://www.openobserve.ai/).
 * Copyright 2016-Present OpenObserve, Inc.
 */

package com.openobserve.android.core.integration.tests

import java.util.concurrent.TimeUnit

@Suppress("UtilityClassWithPublicConstructor")
abstract class BaseTest {

    companion object {
        internal val MEDIUM_WAIT_MS = TimeUnit.SECONDS.toMillis(10)
        internal const val SHORT_WAIT_MS = 100L
    }
}
