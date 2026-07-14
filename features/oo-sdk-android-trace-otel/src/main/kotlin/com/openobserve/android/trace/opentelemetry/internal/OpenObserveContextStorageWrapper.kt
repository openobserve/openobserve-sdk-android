/*
 * Unless explicitly stated otherwise all files in this repository are licensed under the Apache License Version 2.0.
 * This product includes software developed at OpenObserve (https://www.openobserve.ai/).
 * Copyright 2016-Present OpenObserve, Inc.
 */

package com.openobserve.android.trace.opentelemetry.internal

import android.os.Build
import androidx.annotation.RequiresApi
import io.opentelemetry.context.ContextStorage
import java.util.function.Function

@RequiresApi(Build.VERSION_CODES.N)
internal class OpenObserveContextStorageWrapper : Function<ContextStorage, OpenObserveContextStorage> {
    override fun apply(wrapped: ContextStorage): OpenObserveContextStorage {
        return if (wrapped is OpenObserveContextStorage) wrapped else OpenObserveContextStorage(wrapped)
    }
}
