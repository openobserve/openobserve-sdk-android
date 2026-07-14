/*
 * Unless explicitly stated otherwise all files in this repository are licensed under the Apache License Version 2.0.
 * This product includes software developed at OpenObserve (https://www.openobserve.ai/).
 * Copyright 2016-Present OpenObserve, Inc.
 */

package com.openobserve.android.sdk.rules

import com.google.gson.JsonElement
import okhttp3.Headers
import okio.Buffer

data class HandledRequest(
    val url: String? = null,
    val headers: Headers? = null,
    val method: String? = null,
    val jsonBody: JsonElement? = null,
    val textBody: String? = null,
    val requestBuffer: Buffer
)
