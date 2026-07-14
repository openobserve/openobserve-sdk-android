/*
 * Unless explicitly stated otherwise all files in this repository are licensed under the Apache License Version 2.0.
 * This product includes software developed at OpenObserve (https://www.openobserve.ai/).
 * Copyright 2016-Present OpenObserve, Inc.
 */

package com.openobserve.android.core.internal.system

internal class NoOpAppVersionProvider : AppVersionProvider {
    @Suppress("UNUSED_PARAMETER")
    override var version: String
        get() = ""
        set(value) {}
    override val versionCode: Int
        get() = 0
}
