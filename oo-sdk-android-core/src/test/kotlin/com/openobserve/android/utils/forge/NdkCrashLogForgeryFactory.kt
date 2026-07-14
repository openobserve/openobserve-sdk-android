/*
 * Unless explicitly stated otherwise all files in this repository are licensed under the Apache License Version 2.0.
 * This product includes software developed at OpenObserve (https://www.openobserve.ai/).
 * Copyright 2016-Present OpenObserve, Inc.
 */

package com.openobserve.android.utils.forge

import com.openobserve.android.ndk.internal.NdkCrashLog
import fr.xgouchet.elmyr.Forge
import fr.xgouchet.elmyr.ForgeryFactory

internal class NdkCrashLogForgeryFactory :
    ForgeryFactory<NdkCrashLog> {
    override fun getForgery(forge: Forge): NdkCrashLog {
        return NdkCrashLog(
            signal = forge.anInt(min = 1),
            timestamp = System.currentTimeMillis(),
            timeSinceAppStartMs = forge.aNullable { aPositiveLong() },
            signalName = forge.anAlphabeticalString(),
            message = forge.anAlphabeticalString(),
            stacktrace = forge.anAlphabeticalString()
        )
    }
}
