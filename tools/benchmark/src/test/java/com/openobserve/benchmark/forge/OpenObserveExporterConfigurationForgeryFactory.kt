/*
 * Unless explicitly stated otherwise all files in this repository are licensed under the Apache License Version 2.0.
 * This product includes software developed at Datadog (https://www.datadoghq.com/).
 * Copyright 2016-Present Datadog, Inc.
 */

package com.openobserve.benchmark.forge

import com.openobserve.benchmark.OpenObserveExporterConfiguration
import com.openobserve.benchmark.EndPoint
import fr.xgouchet.elmyr.Forge
import fr.xgouchet.elmyr.ForgeryFactory

class OpenObserveExporterConfigurationForgeryFactory : ForgeryFactory<OpenObserveExporterConfiguration> {
    override fun getForgery(forge: Forge): OpenObserveExporterConfiguration {
        return OpenObserveExporterConfiguration(
            serviceName = forge.aString(),
            resource = forge.anAsciiString(),
            applicationId = forge.aString(),
            applicationVersion = forge.anAsciiString(),
            apiKey = forge.anAsciiString(),
            run = forge.aString(),
            scenario = forge.aString(),
            endPoint = forge.aValueFrom(EndPoint::class.java),
            intervalInSeconds = forge.aLong()
        )
    }
}
