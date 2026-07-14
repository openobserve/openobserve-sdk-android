/*
 * Unless explicitly stated otherwise all files in this repository are licensed under the Apache License Version 2.0.
 * This product includes software developed at OpenObserve (https://www.openobserve.ai/).
 * Copyright 2016-Present OpenObserve, Inc.
 */

package com.openobserve.android.sample.data.model

import com.google.gson.annotations.SerializedName

internal data class Log(
    @SerializedName("id") val id: String,
    @SerializedName("attributes") val attributes: LogAttributes
)
