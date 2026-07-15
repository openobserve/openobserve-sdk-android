/*
 * Unless explicitly stated otherwise all files in this repository are licensed under the Apache License Version 2.0.
 * This product includes software developed at Datadog (https://www.datadoghq.com/).
 * Copyright 2016-Present Datadog, Inc.
 */

package com.openobserve.android.sample.webview

import com.openobserve.android.OpenObserveSite
import com.openobserve.android.sample.BuildConfig
import timber.log.Timber

internal val BROWSER_SITE: String
    get() {
        return try {
            OpenObserveSite.valueOf(BuildConfig.DD_SITE_NAME)
        } catch (e: IllegalArgumentException) {
            Timber.e("Error setting site to ${BuildConfig.DD_SITE_NAME}")
            null
        }.browserSite()
    }

private fun OpenObserveSite?.browserSite(): String {
    return when (this) {
        OpenObserveSite.US1,
        OpenObserveSite.STAGING,
        null -> "openobserve.ai"

        OpenObserveSite.US3 -> "us3.openobserve.ai"
        OpenObserveSite.US5 -> "us5.openobserve.ai"
        OpenObserveSite.EU1 -> "openobserve.eu"
        OpenObserveSite.AP1 -> "ap1.openobserve.ai"
        OpenObserveSite.AP2 -> "ap2.openobserve.ai"
        OpenObserveSite.US1_FED -> "openobserve-gov.com"
        OpenObserveSite.US2_FED -> "us2.openobserve-gov.com"
    }
}
