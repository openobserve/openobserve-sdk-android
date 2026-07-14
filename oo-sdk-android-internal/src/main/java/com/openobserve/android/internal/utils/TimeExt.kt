/*
 * Unless explicitly stated otherwise all files in this repository are licensed under the Apache License Version 2.0.
 * This product includes software developed at OpenObserve (https://www.openobserve.ai/).
 * Copyright 2016-Present OpenObserve, Inc.
 */

package com.openobserve.android.internal.utils

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

/**
 * Formats the given epoch milliseconds to an ISO-8601 formatted string in UTC timezone.
 *
 * Example: 2020-02-14T12:34:56.000Z
 *
 * @param epochMillis The time in milliseconds to format.
 * @return The formatted date string.
 */
@Suppress("UnsafeThirdPartyFunctionCall")
fun formatIsoUtc(epochMillis: Long): String {
    // NPE cannot happen here, ISO_8601 pattern is valid
    val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US)
    sdf.timeZone = TimeZone.getTimeZone("UTC")
    return sdf.format(Date(epochMillis))
}
