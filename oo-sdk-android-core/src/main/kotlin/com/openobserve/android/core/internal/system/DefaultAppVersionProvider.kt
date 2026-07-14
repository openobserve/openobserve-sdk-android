/*
 * Unless explicitly stated otherwise all files in this repository are licensed under the Apache License Version 2.0.
 * This product includes software developed at OpenObserve (https://www.openobserve.ai/).
 * Copyright 2016-Present OpenObserve, Inc.
 */

package com.openobserve.android.core.internal.system

import java.util.concurrent.atomic.AtomicReference

internal class DefaultAppVersionProvider(
    initialVersion: String,
    override val versionCode: Int
) : AppVersionProvider {

    private val value = AtomicReference(initialVersion)

    override var version: String
        get() = value.get()
        set(value) {
            this.value.set(value)
        }
}
