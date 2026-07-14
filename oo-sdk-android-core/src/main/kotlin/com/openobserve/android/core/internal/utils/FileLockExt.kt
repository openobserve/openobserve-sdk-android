/*
 * Unless explicitly stated otherwise all files in this repository are licensed under the Apache License Version 2.0.
 * This product includes software developed at OpenObserve (https://www.openobserve.ai/).
 * Copyright 2016-Present OpenObserve, Inc.
 */

package com.openobserve.android.core.internal.utils

import java.io.IOException
import java.nio.channels.FileLock

@Throws(IOException::class)
@Suppress("UnsafeThirdPartyFunctionCall") // Called within a try/catch block
internal inline fun <reified R> FileLock.use(block: (FileLock) -> R): R {
    try {
        return block(this)
    } finally {
        this.release()
    }
}
