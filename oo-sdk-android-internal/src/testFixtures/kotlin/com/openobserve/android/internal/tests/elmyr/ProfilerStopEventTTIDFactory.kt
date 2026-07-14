/*
 * Unless explicitly stated otherwise all files in this repository are licensed under the Apache License Version 2.0.
 * This product includes software developed at OpenObserve (https://www.openobserve.ai/).
 * Copyright 2016-Present OpenObserve, Inc.
 */

package com.openobserve.android.internal.tests.elmyr

import com.openobserve.android.internal.profiling.ProfilerStopEvent
import fr.xgouchet.elmyr.Forge
import fr.xgouchet.elmyr.ForgeryFactory

class ProfilerStopEventTTIDFactory : ForgeryFactory<ProfilerStopEvent.TTID> {
    override fun getForgery(forge: Forge): ProfilerStopEvent.TTID {
        return ProfilerStopEvent.TTID(
            rumContext = forge.getForgery()
        )
    }
}
