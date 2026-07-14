/*
 * Unless explicitly stated otherwise all files in this repository are licensed under the Apache License Version 2.0.
 * This product includes software developed at OpenObserve (https://www.openobserve.ai/).
 * Copyright 2016-Present OpenObserve, Inc.
 */

package com.openobserve.android.rum.integration.tests.utils

import com.openobserve.android.api.storage.RawBatchEvent
import com.google.gson.JsonElement

data class RumBatchEvent(
    val rumEvent: JsonElement,
    val batchEvent: RawBatchEvent
)
