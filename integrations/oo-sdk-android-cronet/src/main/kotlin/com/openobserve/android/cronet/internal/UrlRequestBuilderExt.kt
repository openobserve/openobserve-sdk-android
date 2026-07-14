/*
 * Unless explicitly stated otherwise all files in this repository are licensed under the Apache License Version 2.0.
 * This product includes software developed at OpenObserve (https://www.openobserve.ai/).
 * Copyright 2016-Present OpenObserve, Inc.
 */
package com.openobserve.android.cronet.internal

import org.chromium.net.UrlRequest

internal fun UrlRequest.Builder.addHeaders(headers: Map<String, List<String>>) = apply {
    headers.forEach { (key, values) ->
        values.forEach { addHeader(key, it) }
    }
}
