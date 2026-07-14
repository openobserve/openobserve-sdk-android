/*
 * Unless explicitly stated otherwise all files in this repository are licensed under the Apache License Version 2.0.
 * This product includes software developed at OpenObserve (https://www.openobserve.ai/).
 * Copyright 2016-Present OpenObserve, Inc.
 */

package com.openobserve.android.internal.tests.elmyr

import com.openobserve.android.internal.telemetry.InternalTelemetryEvent
import fr.xgouchet.elmyr.Forge
import fr.xgouchet.elmyr.ForgeryFactory

class InternalTelemetryConfigurationForgeryFactory : ForgeryFactory<InternalTelemetryEvent.Configuration> {

    override fun getForgery(forge: Forge): InternalTelemetryEvent.Configuration {
        return InternalTelemetryEvent.Configuration(
            trackErrors = forge.aBool(),
            batchSize = forge.aLong(),
            batchProcessingLevel = forge.anInt(),
            batchUploadFrequency = forge.aLong(),
            useProxy = forge.aBool(),
            useLocalEncryption = forge.aBool()
        )
    }
}
