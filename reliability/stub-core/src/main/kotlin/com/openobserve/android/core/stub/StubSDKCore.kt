/*
 * Unless explicitly stated otherwise all files in this repository are licensed under the Apache License Version 2.0.
 * This product includes software developed at OpenObserve (https://www.openobserve.ai/).
 * Copyright 2016-Present OpenObserve, Inc.
 */

package com.openobserve.android.core.stub

import android.app.Application
import android.content.ContentResolver
import android.content.res.Configuration
import android.content.res.Resources
import android.os.SystemClock
import com.openobserve.android.api.InternalLogger
import com.openobserve.android.api.context.AccountInfo
import com.openobserve.android.api.context.OpenObserveContext
import com.openobserve.android.api.context.NetworkInfo
import com.openobserve.android.api.context.TimeInfo
import com.openobserve.android.api.context.UserInfo
import com.openobserve.android.api.feature.Feature
import com.openobserve.android.api.feature.FeatureScope
import com.openobserve.android.core.InternalSdkCore
import com.openobserve.android.core.internal.net.FirstPartyHostHeaderTypeResolver
import com.openobserve.android.internal.time.TimeProvider
import fr.xgouchet.elmyr.Forge
import org.mockito.Mockito.mock
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.whenever
import java.util.concurrent.ExecutorService
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.TimeUnit
import org.mockito.kotlin.mock as kmock

/**
 * A stub implementation of [InternalSdkCore].
 *
 * It adds several functions to get info about internal state and usage:
 * [eventsWritten], …
 */
@Suppress("UnsafeThirdPartyFunctionCall")
class StubSDKCore(
    private val forge: Forge,
    private val mockContext: Application = mock(),
    private val mockSdkCore: InternalSdkCore = kmock { on { name } doReturn toString() },
    private var ooContext: OpenObserveContext = forge.getForgery<OpenObserveContext>().copy(source = "android")
) : InternalSdkCore by mockSdkCore {

    private val featureScopes = mutableMapOf<String, FeatureScope>()

    private var currentTimestampMillis: Long = 0L
    private var currentNanoTime: Long = 0L

    init {
        val mockResources = mock<Resources>()
        val mockConfiguration = mock<Configuration>()
        val mockContentResolver = mock<ContentResolver>()
        whenever(mockContext.packageName) doReturn forge.anAlphabeticalString()
        whenever(mockContext.resources) doReturn mockResources
        whenever(mockResources.configuration) doReturn mockConfiguration
        whenever(mockContext.contentResolver) doReturn mockContentResolver
        whenever(mockContext.applicationContext) doReturn mockContext
    }

    // region Stub

    /**
     * Lists all the events written by a given feature.
     * @param featureName the name of the feature
     * @return a list of [StubEvent]
     */
    fun eventsWritten(featureName: String): List<StubEvent> {
        return (featureScopes[featureName] as? StubFeatureScope)?.eventsWritten() ?: emptyList()
    }

    /**
     * Lists all the telemetry events written to this sdk instance.
     * @return a list of [StubEvent]
     */
    fun telemetryEventsWritten(): List<StubTelemetryEvent> {
        return (internalLogger as StubInternalLogger).telemetryEventsWritten
    }

    /**
     * Lists all the events sent to the given feature.
     * @param featureName the name of the feature
     * @return a list of objects
     */
    fun eventsReceived(featureName: String): List<Any> {
        return (featureScopes[featureName] as? StubFeatureScope)?.eventsReceived() ?: emptyList()
    }

    /**
     * Stubs the network info visible via the SDK Core.
     * @param networkInfo the network info
     */
    fun stubNetworkInfo(networkInfo: NetworkInfo) {
        ooContext = ooContext.copy(networkInfo = networkInfo)
    }

    /**
     * Stubs the user info visible via the SDK Core.
     * @param userInfo the user info
     */
    fun stubUserInfo(userInfo: UserInfo) {
        ooContext = ooContext.copy(userInfo = userInfo)
    }

    /**
     * Stubs the account info visible via the SDK Core.
     * @param accountInfo the account info
     */
    fun stubAccountInfo(accountInfo: AccountInfo) {
        ooContext = ooContext.copy(accountInfo = accountInfo)
    }

    /**
     * Advances the current time by the specified duration.
     * @param durationMs the duration to advance in milliseconds
     */
    fun advanceTimeBy(durationMs: Long) {
        currentTimestampMillis += durationMs
        currentNanoTime += TimeUnit.MILLISECONDS.toNanos(durationMs)
    }

    /**
     * Stubs a feature with a mock.
     * This is useful when a feature under tests checks for the presence of another one,
     * or sends events to another feature for cross feature communication.
     * @param featureName the name of the feature to mock
     * @param prepare a lambda used to configure how the stubbed feature should behave
     */
    fun stubFeature(featureName: String, prepare: (Feature) -> Unit = {}) {
        registerFeature(
            mock<Feature>().apply {
                whenever(name) doReturn featureName
                prepare(this)
            }
        )
    }

    /**
     * Stubs a feature and its corresponding feature scope with a mock.
     *
     * @param feature The OpenObserve feature being used in core.
     * @param featureScope The feature scope that will be returned by the [getFeature] method.
     */
    fun stubFeatureScope(feature: Feature, featureScope: FeatureScope) {
        // Stop previous registered
        featureScopes[feature.name]?.unwrap<Feature>()?.onStop()

        featureScopes[feature.name] = featureScope

        feature.onInitialize(mockContext)
        mockSdkCore.registerFeature(feature)
    }

    // endregion

    // region InternalSdkCore

    override val firstPartyHostResolver: FirstPartyHostHeaderTypeResolver =
        StubFirstPartyHostHeaderTypeResolver()

    override fun getOpenObserveContext(withFeatureContexts: Set<String>): OpenObserveContext {
        return ooContext
    }

    override val networkInfo: NetworkInfo
        get() = ooContext.networkInfo

    // endregion

    // region FeatureSdkCore

    override val internalLogger: InternalLogger = StubInternalLogger()

    override val timeProvider = object : TimeProvider {
        override fun getDeviceTimestampMillis(): Long = currentTimestampMillis
        override fun getServerTimestampMillis(): Long = 0L
        override fun getDeviceElapsedTimeNanos(): Long = currentNanoTime
        override fun getServerOffsetNanos(): Long = 0L
        override fun getServerOffsetMillis(): Long = 0L
        override fun getDeviceElapsedRealtimeMillis(): Long = SystemClock.elapsedRealtime()
    }

    override fun registerFeature(feature: Feature) {
        stubFeatureScope(feature, StubFeatureScope(feature, { ooContext }))
    }

    override fun getFeature(featureName: String): FeatureScope? {
        mockSdkCore.getFeature(featureName)
        return featureScopes[featureName]
    }

    override fun updateFeatureContext(
        featureName: String,
        useContextThread: Boolean,
        updateCallback: (context: MutableMap<String, Any?>) -> Unit
    ) {
        val featureContext = ooContext.featuresContext[featureName]?.toMutableMap() ?: mutableMapOf()
        updateCallback(featureContext)
        ooContext = ooContext.copy(
            featuresContext = ooContext.featuresContext.toMutableMap().apply {
                put(featureName, featureContext)
            }
        )
    }

    override fun getFeatureContext(featureName: String, useContextThread: Boolean): Map<String, Any?> {
        return ooContext.featuresContext[featureName].orEmpty()
    }

    override fun createScheduledExecutorService(executorContext: String): ScheduledExecutorService {
        return StubScheduledExecutorService(executorContext, timeProvider::getDeviceTimestampMillis)
    }

    override fun createSingleThreadExecutorService(executorContext: String): ExecutorService {
        return StubExecutorService(executorContext)
    }

    // endregion

    // region SdkCore

    override val service: String
        get() {
            return ooContext.service
        }

    override val time: TimeInfo = mock()

    override fun setUserInfo(
        id: String,
        name: String?,
        email: String?,
        extraInfo: Map<String, Any?>
    ) {
        stubUserInfo(UserInfo(null, id, name, email, extraInfo))
    }

    override fun clearUserInfo() {
        stubUserInfo(UserInfo())
    }

    override fun setAccountInfo(
        id: String,
        name: String?,
        extraInfo: Map<String, Any?>
    ) {
        stubAccountInfo(AccountInfo(id, name, extraInfo))
    }

    override fun clearAccountInfo() {
        ooContext = ooContext.copy(accountInfo = null)
    }

    // endregion
}
