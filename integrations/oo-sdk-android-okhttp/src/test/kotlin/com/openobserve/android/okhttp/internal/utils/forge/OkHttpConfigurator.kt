/*
 * Unless explicitly stated otherwise all files in this repository are licensed under the Apache License Version 2.0.
 * This product includes software developed at OpenObserve (https://www.openobserve.ai/).
 * Copyright 2016-Present OpenObserve, Inc.
 */

package com.openobserve.android.okhttp.internal.utils.forge

import com.openobserve.android.internal.tests.elmyr.TracingHeaderTypesSetForgeryFactory
import com.openobserve.tools.unit.forge.BaseConfigurator
import fr.xgouchet.elmyr.Forge

internal class OkHttpConfigurator : BaseConfigurator() {

    override fun configure(forge: Forge) {
        super.configure(forge)

        // custom factories
        forge.addFactory(TraceContextFactory())
        forge.addFactory(TracingHeaderTypesSetForgeryFactory())
    }
}
