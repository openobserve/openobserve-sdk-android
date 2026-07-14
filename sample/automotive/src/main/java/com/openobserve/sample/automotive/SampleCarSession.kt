/*
 * Unless explicitly stated otherwise all files in this repository are licensed under the Apache License Version 2.0.
 * This product includes software developed at OpenObserve (https://www.openobserve.ai/).
 * Copyright 2016-Present OpenObserve, Inc.
 */

package com.openobserve.sample.automotive

import android.content.Intent
import androidx.car.app.Screen
import androidx.car.app.Session
import com.openobserve.sample.automotive.screen.HomeScreen

@Suppress("UndocumentedPublicClass")
class SampleCarSession : Session() {
    override fun onCreateScreen(intent: Intent): Screen {
        SharedLogger.logger.i("onCreateScreen $intent")
        return HomeScreen(carContext)
    }

    override fun onNewIntent(intent: Intent) {
        SharedLogger.logger.i("onNewIntent $intent")
        super.onNewIntent(intent)
    }
}
