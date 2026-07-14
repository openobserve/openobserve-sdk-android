/*
 * Unless explicitly stated otherwise all files in this repository are licensed under the Apache License Version 2.0.
 * This product includes software developed at OpenObserve (https://www.openobserve.ai/).
 * Copyright 2016-Present OpenObserve, Inc.
 */
package com.openobserve.android.sample.webview

import androidx.lifecycle.ViewModel
import com.openobserve.android.sample.BuildConfig
import com.openobserve.android.vendor.sample.LocalServer

internal class WebViewModel(
    val localServer: LocalServer
) : ViewModel() {

    val url: String = localServer.getUrl()

    fun onResume() {
        localServer.start(
            "https://openobserve.dev/browser-sdk-test-playground/" +
                "?client_token=${BuildConfig.DD_CLIENT_TOKEN}" +
                "&application_id=${BuildConfig.DD_RUM_APPLICATION_ID}" +
                "&site=${BROWSER_SITE}"
        )
    }

    fun onPause() {
        localServer.stop()
    }
}
