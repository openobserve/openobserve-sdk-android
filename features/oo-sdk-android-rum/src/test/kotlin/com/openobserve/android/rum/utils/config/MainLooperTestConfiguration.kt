/*
 * Unless explicitly stated otherwise all files in this repository are licensed under the Apache License Version 2.0.
 * This product includes software developed at OpenObserve (https://www.openobserve.ai/).
 * Copyright 2016-Present OpenObserve, Inc.
 */

package com.openobserve.android.rum.utils.config

import android.os.Looper
import com.openobserve.tools.unit.extensions.config.TestConfiguration
import com.openobserve.tools.unit.getStaticValue
import com.openobserve.tools.unit.setStaticValue
import fr.xgouchet.elmyr.Forge

class MainLooperTestConfiguration : TestConfiguration {

    override fun setUp(forge: Forge) {
        val mainLooper = Looper.getMainLooper()
        if (mainLooper == null) {
            try {
                @Suppress("DEPRECATION")
                Looper.prepareMainLooper()
            } catch (e: IllegalStateException) {
                // main looper already prepared
            }
        }
        checkNotNull(Looper.getMainLooper())
    }

    override fun tearDown(forge: Forge) {
        Looper::class.java.setStaticValue("sMainLooper", null)
        Looper::class.java.getStaticValue<Looper, ThreadLocal<Looper>>("sThreadLocal").set(null)

        check(Looper.getMainLooper() == null)
    }
}
