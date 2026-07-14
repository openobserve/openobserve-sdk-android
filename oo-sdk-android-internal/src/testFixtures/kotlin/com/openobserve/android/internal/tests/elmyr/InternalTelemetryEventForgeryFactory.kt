/*
 * Unless explicitly stated otherwise all files in this repository are licensed under the Apache License Version 2.0.
 * This product includes software developed at OpenObserve (https://www.openobserve.ai/).
 * Copyright 2016-Present OpenObserve, Inc.
 */

package com.openobserve.android.internal.tests.elmyr

import com.openobserve.android.internal.telemetry.InternalTelemetryEvent
import fr.xgouchet.elmyr.Forge
import fr.xgouchet.elmyr.ForgeryFactory

class InternalTelemetryEventForgeryFactory : ForgeryFactory<InternalTelemetryEvent> {

    override fun getForgery(forge: Forge): InternalTelemetryEvent {
        val random = forge.anInt(min = 0, max = 7)
        return when (random) {
            0 -> forge.getForgery<InternalTelemetryEvent.Log.Debug>()

            1 -> forge.getForgery<InternalTelemetryEvent.Log.Error>()

            2 -> forge.getForgery<InternalTelemetryEvent.Configuration>()
            3 -> InternalTelemetryEvent.InterceptorInstantiated
            4 -> forge.getForgery<InternalTelemetryEvent.Metric>()
            5 -> InternalTelemetryEvent.ResourceHeadersTrackingConfigured(
                forge.aValueFrom(InternalTelemetryEvent.ResourceHeadersTrackingConfigured.Mode::class.java)
            )
            else -> forge.getForgery<InternalTelemetryEvent.ApiUsage>()
        }
    }
}
