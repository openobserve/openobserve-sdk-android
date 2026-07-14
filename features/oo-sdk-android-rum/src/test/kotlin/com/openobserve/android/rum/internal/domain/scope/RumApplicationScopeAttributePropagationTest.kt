/*
 * Unless explicitly stated otherwise all files in this repository are licensed under the Apache License Version 2.0.
 * This product includes software developed at OpenObserve (https://www.openobserve.ai/).
 * Copyright 2016-Present OpenObserve, Inc.
 */

package com.openobserve.android.rum.internal.domain.scope

import com.openobserve.android.api.InternalLogger
import com.openobserve.android.api.context.OpenObserveContext
import com.openobserve.android.api.context.NetworkInfo
import com.openobserve.android.api.context.TimeInfo
import com.openobserve.android.api.feature.EventWriteScope
import com.openobserve.android.api.feature.FeatureScope
import com.openobserve.android.api.storage.DataWriter
import com.openobserve.android.core.internal.net.FirstPartyHostHeaderTypeResolver
import com.openobserve.android.core.sampling.Sampler
import com.openobserve.android.rum.RumSessionListener
import com.openobserve.android.rum.RumSessionType
import com.openobserve.android.rum.internal.FeaturesContextResolver
import com.openobserve.android.rum.internal.domain.InfoProvider
import com.openobserve.android.rum.internal.domain.Time
import com.openobserve.android.rum.internal.domain.accessibility.AccessibilitySnapshotManager
import com.openobserve.android.rum.internal.domain.battery.BatteryInfo
import com.openobserve.android.rum.internal.domain.display.DisplayInfo
import com.openobserve.android.rum.internal.instrumentation.insights.InsightsCollector
import com.openobserve.android.rum.internal.metric.SessionMetricDispatcher
import com.openobserve.android.rum.internal.metric.slowframes.SlowFramesListener
import com.openobserve.android.rum.internal.startup.RumAppStartupTelemetryReporter
import com.openobserve.android.rum.internal.vitals.VitalMonitor
import com.openobserve.android.rum.metric.interactiontonextview.LastInteractionIdentifier
import com.openobserve.android.rum.metric.networksettled.InitialResourceIdentifier
import com.openobserve.android.rum.model.ViewEvent
import com.openobserve.android.rum.utils.config.GlobalRumMonitorTestConfiguration
import com.openobserve.android.rum.utils.forge.Configurator
import com.openobserve.tools.unit.annotations.TestConfigurationsProvider
import com.openobserve.tools.unit.extensions.TestConfigurationExtension
import com.openobserve.tools.unit.extensions.config.TestConfiguration
import com.openobserve.tools.unit.forge.exhaustiveAttributes
import fr.xgouchet.elmyr.Forge
import fr.xgouchet.elmyr.annotation.BoolForgery
import fr.xgouchet.elmyr.annotation.FloatForgery
import fr.xgouchet.elmyr.annotation.Forgery
import fr.xgouchet.elmyr.annotation.StringForgery
import fr.xgouchet.elmyr.junit5.ForgeConfiguration
import fr.xgouchet.elmyr.junit5.ForgeExtension
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.junit.jupiter.api.extension.Extensions
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.junit.jupiter.MockitoSettings
import org.mockito.kotlin.any
import org.mockito.kotlin.doAnswer
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever
import org.mockito.quality.Strictness
import java.util.concurrent.TimeUnit
import kotlin.math.max
import kotlin.math.min

@Extensions(
    ExtendWith(MockitoExtension::class),
    ExtendWith(ForgeExtension::class),
    ExtendWith(TestConfigurationExtension::class)
)
@MockitoSettings(strictness = Strictness.LENIENT)
@ForgeConfiguration(Configurator::class)
internal class RumApplicationScopeAttributePropagationTest {

    lateinit var testedScope: RumApplicationScope

    @Mock
    lateinit var mockParentScope: RumScope

    @Mock
    lateinit var mockWriter: DataWriter<Any>

    @Mock
    lateinit var mockResolver: FirstPartyHostHeaderTypeResolver

    @Mock
    lateinit var mockSessionListener: RumSessionListener

    @Mock
    lateinit var mockLastInteractionIdentifier: LastInteractionIdentifier

    @Mock
    lateinit var mockCpuVitalMonitor: VitalMonitor

    @Mock
    lateinit var mockMemoryVitalMonitor: VitalMonitor

    @Mock
    lateinit var mockFrameRateVitalMonitor: VitalMonitor

    @Mock
    lateinit var mockInternalLogger: InternalLogger

    @Mock
    lateinit var mockRumFeatureScope: FeatureScope

    @Mock
    lateinit var mockEventWriteScope: EventWriteScope

    @Mock
    lateinit var mockFeaturesContextResolver: FeaturesContextResolver

    @Mock
    lateinit var mockSessionEndedMetricDispatcher: SessionMetricDispatcher

    @Mock
    lateinit var mockInitialResourceIdentifier: InitialResourceIdentifier

    @Mock
    lateinit var mockAccessibilitySnapshotManager: AccessibilitySnapshotManager

    @Mock
    lateinit var mockBatteryInfoProvider: InfoProvider<BatteryInfo>

    @Mock
    lateinit var mockDisplayInfoProvider: InfoProvider<DisplayInfo>

    @Mock
    lateinit var mockRumAppStartupTelemetryReporter: RumAppStartupTelemetryReporter

    @Mock
    private lateinit var mockInsightsCollector: InsightsCollector

    @Mock
    lateinit var mockSlowFramesListener: SlowFramesListener

    @Mock
    lateinit var mockSessionSampler: Sampler<String>

    lateinit var fakeEventTime: Time

    lateinit var fakeEvent: RumRawEvent

    @Forgery
    lateinit var fakeTimeInfoAtScopeStart: TimeInfo

    @Forgery
    lateinit var fakeNetworkInfoAtScopeStart: NetworkInfo

    @Forgery
    lateinit var fakeOpenObserveContext: OpenObserveContext

    lateinit var fakeGlobalAttributes: Map<String, Any?>

    @BoolForgery
    var fakeHasReplay: Boolean = false

    @FloatForgery(min = 0f, max = 100f)
    var fakeSampleRate: Float = 0f

    @BoolForgery
    var fakeBackgroundTrackingEnabled: Boolean = false

    @BoolForgery
    var fakeTrackFrustrations: Boolean = true

    @StringForgery
    lateinit var fakeApplicationId: String

    private var fakeRumSessionType: RumSessionType? = null

    @BeforeEach
    fun `set up`(forge: Forge) {
        fakeGlobalAttributes = forge.exhaustiveAttributes()

        fakeOpenObserveContext = fakeOpenObserveContext.copy(
            source = forge.aValueFrom(ViewEvent.ViewEventSource::class.java).toJson().asString
        )

        val fakeOffset = -forge.aLong(1000, 50000)
        val fakeTimestamp = System.currentTimeMillis() + fakeOffset
        val fakeNanos = System.nanoTime() + TimeUnit.MILLISECONDS.toNanos(fakeOffset)
        val maxLimit = max(Long.MAX_VALUE - fakeTimestamp, Long.MAX_VALUE)
        val minLimit = min(-fakeTimestamp, maxLimit)

        fakeOpenObserveContext = fakeOpenObserveContext.copy(
            time = fakeTimeInfoAtScopeStart.copy(
                serverTimeOffsetMs = forge.aLong(min = minLimit, max = maxLimit)
            )
        )
        fakeEventTime = Time(fakeTimestamp, fakeNanos)

        whenever(mockRumFeatureScope.withWriteContext(any(), any())) doAnswer {
            val callback = it.getArgument<(OpenObserveContext, EventWriteScope) -> Unit>(1)
            callback.invoke(fakeOpenObserveContext, mockEventWriteScope)
        }

        whenever(rumMonitor.mockSdkCore.internalLogger) doReturn mock()
        whenever(rumMonitor.mockSdkCore.timeProvider) doReturn mock()
        fakeRumSessionType = forge.aNullable { aValueFrom(RumSessionType::class.java) }
        whenever(mockSessionSampler.getSampleRate()).thenReturn(fakeSampleRate)
        whenever(mockSessionSampler.sample(any())).thenReturn(true)
        testedScope = RumApplicationScope(
            applicationId = fakeApplicationId,
            sdkCore = rumMonitor.mockSdkCore,
            sessionSampler = mockSessionSampler,
            backgroundTrackingEnabled = fakeBackgroundTrackingEnabled,
            trackFrustrations = fakeTrackFrustrations,
            firstPartyHostHeaderTypeResolver = mockResolver,
            cpuVitalMonitor = mockCpuVitalMonitor,
            memoryVitalMonitor = mockMemoryVitalMonitor,
            frameRateVitalMonitor = mockFrameRateVitalMonitor,
            sessionEndedMetricDispatcher = mockSessionEndedMetricDispatcher,
            sessionListener = mockSessionListener,
            initialResourceIdentifier = mockInitialResourceIdentifier,
            lastInteractionIdentifier = mockLastInteractionIdentifier,
            slowFramesListener = mockSlowFramesListener,
            rumSessionTypeOverride = fakeRumSessionType,
            accessibilitySnapshotManager = mockAccessibilitySnapshotManager,
            batteryInfoProvider = mockBatteryInfoProvider,
            displayInfoProvider = mockDisplayInfoProvider,
            rumSessionScopeStartupManagerFactory = mock(),
            insightsCollector = mockInsightsCollector
        )
    }

    // region Propagate parent attributes

    @Test
    fun `M return global attributes W getCustomAttributes()`() {
        // Given
        whenever(rumMonitor.mockInstance.getAttributes()) doReturn fakeGlobalAttributes

        // When
        val customAttributes = testedScope.getCustomAttributes()

        // Then
        assertThat(customAttributes)
            .containsExactlyInAnyOrderEntriesOf(fakeGlobalAttributes)
    }

    // endregion

    companion object {

        val rumMonitor = GlobalRumMonitorTestConfiguration()

        @TestConfigurationsProvider
        @JvmStatic
        fun getTestConfigurations(): List<TestConfiguration> {
            return listOf(rumMonitor)
        }
    }
}
