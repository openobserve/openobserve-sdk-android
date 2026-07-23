/*
 * Unless explicitly stated otherwise all files in this repository are licensed under the Apache License Version 2.0.
 * This product includes software developed at Datadog (https://www.datadoghq.com/).
 * Copyright 2016-Present Datadog, Inc.
 */

package com.openobserve.android.utils.extension

import com.openobserve.android.log.internal.domain.OpenObserveLogGenerator
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.TimeZone

fun Long.toIsoFormattedTimestamp(iso: String = OpenObserveLogGenerator.ISO_8601): String {
    val simpleDateFormat = SimpleDateFormat(iso, Locale.US).apply {
        timeZone = TimeZone.getTimeZone("UTC")
    }
    return simpleDateFormat.format(this)
}
