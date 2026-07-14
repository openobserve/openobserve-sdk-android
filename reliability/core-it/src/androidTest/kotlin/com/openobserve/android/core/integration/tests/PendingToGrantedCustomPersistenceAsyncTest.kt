/*
 * Unless explicitly stated otherwise all files in this repository are licensed under the Apache License Version 2.0.
 * This product includes software developed at OpenObserve (https://www.openobserve.ai/).
 * Copyright 2016-Present OpenObserve, Inc.
 */

package com.openobserve.android.core.integration.tests

import androidx.test.core.app.ApplicationProvider
import com.openobserve.android.OpenObserve
import com.openobserve.android.OpenObserveSite
import com.openobserve.android._InternalProxy
import com.openobserve.android.api.feature.Feature
import com.openobserve.android.api.feature.stub.StubStorageBackedFeature
import com.openobserve.android.api.storage.EventType
import com.openobserve.android.api.storage.RawBatchEvent
import com.openobserve.android.core.InternalSdkCore
import com.openobserve.android.core.configuration.BatchProcessingLevel
import com.openobserve.android.core.configuration.BatchSize
import com.openobserve.android.core.configuration.Configuration
import com.openobserve.android.core.configuration.UploadFrequency
import com.openobserve.android.core.integration.tests.assertj.MockWebServerAssert
import com.openobserve.android.core.integration.tests.forge.factories.ConfigurationCoreForgeryFactory
import com.openobserve.android.core.integration.tests.utils.HeapBasedPersistenceStrategy
import com.openobserve.android.core.persistence.PersistenceStrategy
import com.openobserve.android.privacy.TrackingConsent
import com.openobserve.android.trace.TracingHeaderType
import com.openobserve.tools.unit.ConditionWatcher
import com.openobserve.tools.unit.forge.useToolsFactories
import com.google.gson.JsonObject
import fr.xgouchet.elmyr.annotation.StringForgery
import fr.xgouchet.elmyr.annotation.StringForgeryType
import fr.xgouchet.elmyr.junit4.ForgeRule
import fr.xgouchet.elmyr.jvm.useJvmFactories
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized
import org.junit.runners.Parameterized.Parameters
import java.util.UUID
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

/**
 * Instrumentation tests for the feature scope for the case when the tracking consent is switched from Pending to
 * Granted. These tests are meant to assess the correct behavior of our persistence strategy
 * when write events are sent to the feature scope and the tracking consent is switched from Pending to Granted from
 * 2 different threads.
 * This test suite focuses on the custom persistence strategy configuration feature.
 */
@RunWith(Parameterized::class)
class PendingToGrantedCustomPersistenceAsyncTest(
    private val fakeBatchData: List<RawBatchEvent>,
    private val fakeBatchMetadata: ByteArray,
    private val fakeConfiguration: Configuration,
    private val eventType: EventType
) : MockServerTest() {

    @get:Rule
    var forge = ForgeRule()
        .useJvmFactories()
        .useToolsFactories()
        .withFactory(ConfigurationCoreForgeryFactory())

    @StringForgery(type = StringForgeryType.ALPHABETICAL)
    lateinit var fakeFeatureName: String

    private lateinit var trackingConsent: TrackingConsent
    private lateinit var stubFeature: Feature
    private lateinit var testedInternalSdkCore: InternalSdkCore

    @Before
    fun setUp() {
        stubFeature = StubStorageBackedFeature(
            forge,
            fakeFeatureName,
            getMockServerWrapper().getServerUrl()
        )
    }

    @After
    fun tearDown() {
        cleanStorage()
        OpenObserve.stopInstance()
        cleanMockWebServer()
    }

    @Test
    fun mustReceiveTheEvents_whenFeatureWrite_customStorage_asynchronousAccess() {
        // Given
        trackingConsent = TrackingConsent.PENDING
        testedInternalSdkCore = OpenObserve.initialize(
            context = ApplicationProvider.getApplicationContext(),
            configuration = fakeConfiguration,
            trackingConsent = trackingConsent
        ) as InternalSdkCore
        testedInternalSdkCore.registerFeature(stubFeature)
        val featureScope = testedInternalSdkCore.getFeature(fakeFeatureName)
        checkNotNull(featureScope)
        val countDownLatch = CountDownLatch(2)

        // When
        Thread {
            Thread.sleep(200)
            OpenObserve.setTrackingConsent(TrackingConsent.GRANTED)
            countDownLatch.countDown()
        }.start()
        Thread {
            fakeBatchData.forEach { rawBatchEvent ->
                featureScope.withWriteContext { _, writeScope ->
                    writeScope {
                        it.write(
                            rawBatchEvent,
                            fakeBatchMetadata,
                            eventType
                        )
                    }
                }
            }
            countDownLatch.countDown()
        }.start()

        // Then
        countDownLatch.await(TimeUnit.SECONDS.toMillis(10), TimeUnit.MILLISECONDS)
        ConditionWatcher {
            MockWebServerAssert.assertThat(getMockServerWrapper())
                .withConfiguration(fakeConfiguration)
                .withTrackingConsent(TrackingConsent.GRANTED)
                .receivedData(fakeBatchData, fakeBatchMetadata)
            true
        }.doWait(MEDIUM_WAIT_MS)
    }

    // region Internal

    private fun cleanStorage() {
        val storageFolder = testedInternalSdkCore.rootStorageDir
        storageFolder?.deleteRecursively()
    }

    companion object {
        private val forge = ForgeRule()
            .useJvmFactories()
            .useToolsFactories()
            .withFactory(ConfigurationCoreForgeryFactory())

        private fun ForgeRule.forgeConfigurationWithCustomStorage(): Configuration {
            return Configuration.Builder(
                UUID.randomUUID().toString(),
                anHexadecimalString(),
                anHexadecimalString(),
                aNullable {
                    anAlphaNumericalString()
                }
            )
                .setUseDeveloperModeWhenDebuggable(aBool())
                // this needs to be before allowing the clear text traffic as it invalidates this option
                .useSite(aValueFrom(OpenObserveSite::class.java))
                .setFirstPartyHostsWithHeaderType(
                    aMap {
                        val fakeUrl = aStringMatching("https://[a-z0-9]+\\.com")
                        fakeUrl to aList {
                            aValueFrom(
                                TracingHeaderType::class.java
                            )
                        }.toSet()
                    }
                )
                .apply {
                    _InternalProxy.allowClearTextHttp(this)
                }
                .setBatchSize(BatchSize.SMALL)
                .setUploadFrequency(UploadFrequency.FREQUENT)
                .setBatchProcessingLevel(BatchProcessingLevel.HIGH)
                .setPersistenceStrategyFactory(object : PersistenceStrategy.Factory {
                    override fun create(
                        identifier: String,
                        maxItemsPerBatch: Int,
                        maxBatchSize: Long
                    ): PersistenceStrategy {
                        return HeapBasedPersistenceStrategy()
                    }
                })
                .build()
        }

        @JvmStatic
        @Parameters
        fun testParameters(): Collection<Array<Any>> {
            return listOf(
                arrayOf(
                    forge.aList(size = forge.anInt(min = 50, max = 100)) {
                        val fakeEvent: JsonObject = forge.getForgery()
                        val eventMetadata = forge.anAlphabeticalString()
                        RawBatchEvent(
                            fakeEvent.toString().toByteArray(),
                            eventMetadata.toByteArray()
                        )
                    },
                    forge.anAlphabeticalString().toByteArray(),
                    forge.forgeConfigurationWithCustomStorage(),
                    forge.aValueFrom(EventType::class.java)
                ),
                arrayOf(
                    forge.aList(size = forge.anInt(min = 50, max = 100)) {
                        val fakeEvent: JsonObject = forge.getForgery()
                        val eventMetadata = forge.anAlphabeticalString()
                        RawBatchEvent(
                            fakeEvent.toString().toByteArray(),
                            eventMetadata.toByteArray()
                        )
                    },
                    forge.anAlphabeticalString().toByteArray(),
                    forge.forgeConfigurationWithCustomStorage(),
                    forge.aValueFrom(EventType::class.java)
                ),
                arrayOf(
                    forge.aList(size = forge.anInt(min = 50, max = 100)) {
                        val fakeEvent: JsonObject = forge.getForgery()
                        val eventMetadata = forge.anAlphabeticalString()
                        RawBatchEvent(
                            fakeEvent.toString().toByteArray(),
                            eventMetadata.toByteArray()
                        )
                    },
                    forge.anAlphabeticalString().toByteArray(),
                    forge.getForgery<Configuration>(),
                    forge.aValueFrom(EventType::class.java)
                ),
                arrayOf(
                    forge.aList(size = forge.anInt(min = 50, max = 100)) {
                        val fakeEvent: JsonObject = forge.getForgery()
                        val eventMetadata = forge.anAlphabeticalString()
                        RawBatchEvent(
                            fakeEvent.toString().toByteArray(),
                            eventMetadata.toByteArray()
                        )
                    },
                    forge.anAlphabeticalString().toByteArray(),
                    forge.forgeConfigurationWithCustomStorage(),
                    forge.aValueFrom(EventType::class.java)
                ),
                arrayOf(
                    forge.aList(size = forge.anInt(min = 50, max = 100)) {
                        val fakeEvent: JsonObject = forge.getForgery()
                        val eventMetadata = forge.anAlphabeticalString()
                        RawBatchEvent(
                            fakeEvent.toString().toByteArray(),
                            eventMetadata.toByteArray()
                        )
                    },
                    forge.anAlphabeticalString().toByteArray(),
                    forge.forgeConfigurationWithCustomStorage(),
                    forge.aValueFrom(EventType::class.java)
                )
            )
        }
    }

    // endregion
}
