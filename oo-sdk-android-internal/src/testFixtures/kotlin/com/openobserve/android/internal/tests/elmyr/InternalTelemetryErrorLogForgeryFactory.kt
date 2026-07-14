/*
 * Unless explicitly stated otherwise all files in this repository are licensed under the Apache License Version 2.0.
 * This product includes software developed at OpenObserve (https://www.openobserve.ai/).
 * Copyright 2016-Present OpenObserve, Inc.
 */

package com.openobserve.android.internal.tests.elmyr

import com.openobserve.android.internal.telemetry.InternalTelemetryEvent
import com.openobserve.tools.unit.forge.aThrowable
import fr.xgouchet.elmyr.Forge
import fr.xgouchet.elmyr.ForgeryFactory

class InternalTelemetryErrorLogForgeryFactory : ForgeryFactory<InternalTelemetryEvent.Log.Error> {

    override fun getForgery(forge: Forge): InternalTelemetryEvent.Log.Error {
        return InternalTelemetryEvent.Log.Error(
            message = forge.aString(),
            additionalProperties = forge.aMap { aString() to aString() },
            error = forge.aNullable { aThrowable() },
            stacktrace = forge.aNullable { aString() },
            kind = forge.aNullable { aString() }
        )
    }
}
