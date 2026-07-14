/*
 * Unless explicitly stated otherwise all files in this repository are licensed under the Apache License Version 2.0.
 * This product includes software developed at OpenObserve (https://www.openobserve.ai/).
 * Copyright 2016-Present OpenObserve, Inc.
 */

package com.openobserve.android.rum.utils.forge

import com.openobserve.android.internal.tests.elmyr.InternalTelemetryApiUsageForgeryFactory
import com.openobserve.android.internal.tests.elmyr.InternalTelemetryConfigurationForgeryFactory
import com.openobserve.android.internal.tests.elmyr.InternalTelemetryDebugLogForgeryFactory
import com.openobserve.android.internal.tests.elmyr.InternalTelemetryErrorLogForgeryFactory
import com.openobserve.android.internal.tests.elmyr.InternalTelemetryEventForgeryFactory
import com.openobserve.android.internal.tests.elmyr.InternalTelemetryMetricForgeryFactory
import com.openobserve.android.internal.tests.elmyr.TracingHeaderTypesSetForgeryFactory
import com.openobserve.android.rum.tests.elmyr.BatteryInfoForgeryFactory
import com.openobserve.android.rum.tests.elmyr.DisplayInfoForgeryFactory
import com.openobserve.android.rum.tests.elmyr.ResourceIdForgeryFactory
import com.openobserve.android.rum.tests.elmyr.RumScopeKeyForgeryFactory
import com.openobserve.android.tests.elmyr.useCoreFactories
import com.openobserve.tools.unit.forge.BaseConfigurator
import fr.xgouchet.elmyr.Forge
import fr.xgouchet.elmyr.jvm.useJvmFactories

internal class Configurator : BaseConfigurator() {

    override fun configure(forge: Forge) {
        super.configure(forge)
        forge.useJvmFactories()

        // Core
        forge.useCoreFactories()

        // RUM
        forge.useCommonRumFactories()
        forge.addFactory(ConfigurationRumForgeryFactory())
        forge.addFactory(RumConfigurationForgeryFactory())
        forge.addFactory(MotionEventForgeryFactory())
        forge.addFactory(RumEventMapperFactory())
        forge.addFactory(RumContextForgeryFactory())
        forge.addFactory(VitalInfoForgeryFactory())
        forge.addFactory(RumEventMetaForgeryFactory())
        forge.addFactory(ViewEventMetaForgeryFactory())
        forge.addFactory(RumScopeKeyForgeryFactory())
        forge.addFactory(ResourceIdForgeryFactory())
        forge.addFactory(InternalResourceContextFactory())
        forge.addFactory(NetworkSettledResourceContextFactory())
        forge.addFactory(InternalInteractionContextFactory())
        forge.addFactory(PreviousViewLastActionContextFactory())
        forge.addFactory(TelemetryViewInitializationMetricsStateForgeryFactory())
        forge.addFactory(FrameDataForgeryFactory())
        forge.addFactory(FrameMetricDataForgeryFactory())
        forge.addFactory(ViewUIPerformanceReportForgeryFactory())
        forge.addFactory(SlowFramesConfigurationForgeryFactory())
        forge.addFactory(DisplayInfoForgeryFactory())
        forge.addFactory(BatteryInfoForgeryFactory())

        // Telemetry schema models
        forge.addFactory(TelemetryDebugEventForgeryFactory())
        forge.addFactory(TelemetryErrorEventForgeryFactory())
        forge.addFactory(TelemetryConfigurationEventForgeryFactory())
        forge.addFactory(TelemetryUsageEventForgeryFactory())

        // Telemetry internal models
        forge.addFactory(InternalTelemetryEventForgeryFactory())
        forge.addFactory(InternalTelemetryMetricForgeryFactory())
        forge.addFactory(InternalTelemetryDebugLogForgeryFactory())
        forge.addFactory(InternalTelemetryErrorLogForgeryFactory())
        forge.addFactory(InternalTelemetryConfigurationForgeryFactory())
        forge.addFactory(InternalTelemetryApiUsageForgeryFactory())
        forge.addFactory(TracingHeaderTypesSetForgeryFactory())

        // RumRawEvent
        forge.addFactory(ActionSentForgeryFactory())
        forge.addFactory(ResourceSentForgeryFactory())
        forge.addFactory(ResourceDroppedForgeryFactory())
        forge.addFactory(AddCustomTimingEventForgeryFactory())
        forge.addFactory(AddErrorEventForgeryFactory())
        forge.addFactory(AddFeatureFlagEvaluationForgeryFactory())
        forge.addFactory(AddFeatureFlagEvaluationsForgeryFactory())
        forge.addFactory(ErrorSentForgeryFactory())
        forge.addFactory(LongTaskSentForgeryFactory())
    }
}
