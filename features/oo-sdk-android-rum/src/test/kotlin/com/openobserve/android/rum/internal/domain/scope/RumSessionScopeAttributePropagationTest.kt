/*
 * Unless explicitly stated otherwise all files in this repository are licensed under the Apache License Version 2.0.
 * This product includes software developed at Datadog (https://www.datadoghq.com/).
 * Copyright 2016-Present Datadog, Inc.
 */

package com.openobserve.android.rum.internal.domain.scope

import com.openobserve.android.api.InternalLogger
import com.openobserve.android.api.context.OpenObserveContext
import com.openobserve.android.api.context.NetworkInfo
import com.openobserve.android.api.feature.FeatureScope
import com.openobserve.android.api.storage.DataWriter
import com.openobserve.android.api.storage.EventBatchWriter
import com.openobserve.android.core.InternalSdkCore
import com.openobserve.android.core.internal.net.FirstPartyHostHeaderTypeResolver
import com.openobserve.android.core.sampling.Sampler
import com.openobserve.android.rum.RumSessionListener
import com.openobserve.android.rum.RumSessionType
import com.openobserve.android.rum.internal.FeaturesContextResolver
import com.openobserve.android.rum.internal.domain.InfoProvider
import com.openobserve.android.rum.internal.domain.RumContext
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
import com.openobserve.android.rum.utils.forge.Configurator
import com.openobserve.tools.unit.forge.exhaustiveAttributes
import fr.xgouchet.elmyr.Forge
import fr.xgouchet.elmyr.annotation.BoolForgery
import fr.xgouchet.elmyr.annotation.FloatForgery
import fr.xgouchet.elmyr.annotation.Forgery
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
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever
import org.mockito.quality.Strictness
import java.util.concurrent.TimeUnit

@Extensions(
    ExtendWith(MockitoExtension::class),
    ExtendWith(ForgeExtension::class)
)
@MockitoSettings(strictness = Strictness.LENIENT)
@ForgeConfiguration(Configurator::class)
internal class RumSessionScopeAttributePropagationTest {

    lateinit var testedScope: RumSessionScope

    @Mock
    lateinit var mockSdkCore: InternalSdkCore

    @Mock
    lateinit var mockParentScope: RumScope

    @Mock
    lateinit var mockWriter: DataWriter<Any>

    @Mock
    lateinit var mockResolver: FirstPartyHostHeaderTypeResolver

    @Mock
    lateinit var mockSessionListener: RumSessionListener

    @Mock
    lateinit var mockNetworkSettledResourceIdentifier: InitialResourceIdentifier

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
    lateinit var mockRumFeatureScope: FeatureScope

    @Mock
    lateinit var mockEventBatchWriter: EventBatchWriter

    @Mock
    lateinit var mockFeaturesContextResolver: FeaturesContextResolver

    @Mock
    lateinit var mockViewChangedListener: RumViewChangedListener

    @Mock
    lateinit var mockSessionEndedMetricDispatcher: SessionMetricDispatcher

    @Mock
    lateinit var mockSlowFramesListener: SlowFramesListener

    @Mock
    lateinit var mockSessionSampler: Sampler<String>

    lateinit var fakeParentAttributes: Map<String, Any?>

    @Forgery
    lateinit var fakeParentContext: RumContext

    lateinit var fakeEventTime: Time

    lateinit var fakeEvent: RumRawEvent

    @Forgery
    lateinit var fakeNetworkInfoAtScopeStart: NetworkInfo

    @Forgery
    lateinit var fakeOpenObserveContext: OpenObserveContext

    @BoolForgery
    var fakeHasReplay: Boolean = false

    @FloatForgery(min = 0f, max = 100f)
    var fakeSampleRate: Float = 0f

    @BoolForgery
    var fakeBackgroundTrackingEnabled: Boolean = false

    @BoolForgery
    var fakeTrackFrustrations: Boolean = true
    private var fakeRumSessionType: RumSessionType? = null

    @BeforeEach
    fun `set up`(forge: Forge) {
        fakeParentAttributes = forge.exhaustiveAttributes()
        whenever(mockParentScope.getCustomAttributes()) doReturn fakeParentAttributes.toMutableMap()

        whenever(mockSdkCore.internalLogger) doReturn mock()
        whenever(mockSdkCore.timeProvider) doReturn mock()
        fakeRumSessionType = forge.aNullable { aValueFrom(RumSessionType::class.java) }
        whenever(mockSessionSampler.getSampleRate()).thenReturn(fakeSampleRate)
        whenever(mockSessionSampler.sample(any())).thenReturn(true)
        testedScope = RumSessionScope(
            parentScope = mockParentScope,
            sdkCore = mockSdkCore,
            sessionEndedMetricDispatcher = mockSessionEndedMetricDispatcher,
            sessionSampler = mockSessionSampler,
            backgroundTrackingEnabled = fakeBackgroundTrackingEnabled,
            trackFrustrations = fakeTrackFrustrations,
            viewChangedListener = mockViewChangedListener,
            firstPartyHostHeaderTypeResolver = mockResolver,
            cpuVitalMonitor = mockCpuVitalMonitor,
            memoryVitalMonitor = mockMemoryVitalMonitor,
            frameRateVitalMonitor = mockFrameRateVitalMonitor,
            sessionListener = mockSessionListener,
            applicationDisplayed = false,
            networkSettledResourceIdentifier = mockNetworkSettledResourceIdentifier,
            lastInteractionIdentifier = mockLastInteractionIdentifier,
            slowFramesListener = mockSlowFramesListener,
            sessionInactivityNanos = TEST_INACTIVITY_NS,
            sessionMaxDurationNanos = TEST_MAX_DURATION_NS,
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
    fun `M return parent attributes W getCustomAttributes()`() {
        // When
        val customAttributes = testedScope.getCustomAttributes()

        // Then
        assertThat(customAttributes)
            .containsExactlyInAnyOrderEntriesOf(fakeParentAttributes)
    }

    // endregion

    companion object {
        private const val TEST_SLEEP_MS = 50L
        private const val TEST_INACTIVITY_MS = TEST_SLEEP_MS * 3
        private const val TEST_MAX_DURATION_MS = TEST_SLEEP_MS * 10

        private val TEST_INACTIVITY_NS = TimeUnit.MILLISECONDS.toNanos(TEST_INACTIVITY_MS)
        private val TEST_MAX_DURATION_NS = TimeUnit.MILLISECONDS.toNanos(TEST_MAX_DURATION_MS)
    }
}
