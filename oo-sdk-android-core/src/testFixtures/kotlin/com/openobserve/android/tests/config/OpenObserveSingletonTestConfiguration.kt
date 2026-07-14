/*
 * Unless explicitly stated otherwise all files in this repository are licensed under the Apache License Version 2.0.
 * This product includes software developed at OpenObserve (https://www.openobserve.ai/).
 * Copyright 2016-Present OpenObserve, Inc.
 */

package com.openobserve.android.tests.config

import com.openobserve.android.OpenObserve
import com.openobserve.android.core.InternalSdkCore
import com.openobserve.tools.unit.extensions.config.MockTestConfiguration
import fr.xgouchet.elmyr.Forge

class OpenObserveSingletonTestConfiguration :
    MockTestConfiguration<InternalSdkCore>(InternalSdkCore::class.java) {

    override fun setUp(forge: Forge) {
        super.setUp(forge)

        OpenObserve.registry.register(null, mockInstance)
    }

    override fun tearDown(forge: Forge) {
        clearRegistry()
        super.tearDown(forge)
    }

    fun clearRegistry() {
        OpenObserve.registry.clear()
    }
}
