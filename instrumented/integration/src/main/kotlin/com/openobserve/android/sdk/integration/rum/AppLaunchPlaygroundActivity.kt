/*
 * Unless explicitly stated otherwise all files in this repository are licensed under the Apache License Version 2.0.
 * This product includes software developed at OpenObserve (https://www.openobserve.ai/).
 * Copyright 2016-Present OpenObserve, Inc.
 */

package com.openobserve.android.sdk.integration.rum

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.openobserve.android.rum.ExperimentalRumApi
import com.openobserve.android.rum.GlobalRumMonitor
import com.openobserve.android.sdk.integration.R

@OptIn(ExperimentalRumApi::class)
internal class AppLaunchPlaygroundActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_activity_layout)
    }

    override fun onResume() {
        super.onResume()

        @Suppress("MagicNumber")
        window.decorView.postDelayed(
            { GlobalRumMonitor.get().reportAppFullyDisplayed() },
            3000
        )
    }
}
