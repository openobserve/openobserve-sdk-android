/*
 * Unless explicitly stated otherwise all files in this repository are licensed under the Apache License Version 2.0.
 * This product includes software developed at OpenObserve (https://www.openobserve.ai/).
 * Copyright 2016-Present OpenObserve, Inc.
 */

package com.openobserve.android.core.internal

import com.openobserve.android.api.context.OpenObserveContext
import com.openobserve.android.api.context.DeviceInfo
import com.openobserve.android.api.context.LocaleInfo
import com.openobserve.android.api.context.ProcessInfo
import com.openobserve.android.core.internal.time.composeTimeInfo

internal class OpenObserveContextProvider(
    private val coreFeature: CoreFeature,
    private val featureContextProvider: FeatureContextProvider
) : ContextProvider {
    @Suppress("LongMethod")
    override fun getContext(withFeatureContexts: Set<String>): OpenObserveContext {
        // IMPORTANT All properties should be immutable and be frozen at the state
        // of the context construction moment
        return OpenObserveContext(
            site = coreFeature.site,
            clientToken = coreFeature.clientToken,
            service = coreFeature.serviceName,
            env = coreFeature.envName,
            version = coreFeature.packageVersionProvider.version,
            versionCode = coreFeature.packageVersionProvider.versionCode,
            variant = coreFeature.variant,
            sdkVersion = coreFeature.sdkVersion,
            source = coreFeature.sourceName,
            time = coreFeature.timeProvider.composeTimeInfo(),
            processInfo = ProcessInfo(
                isMainProcess = coreFeature.isMainProcess
            ),
            networkInfo = coreFeature.networkInfoProvider.getLatestNetworkInfo(),
            deviceInfo = with(coreFeature.androidInfoProvider) {
                DeviceInfo(
                    deviceName = deviceName,
                    deviceBrand = deviceBrand,
                    deviceType = deviceType,
                    deviceModel = deviceModel,
                    deviceBuildId = deviceBuildId,
                    osName = osName,
                    osVersion = osVersion,
                    osMajorVersion = osMajorVersion,
                    architecture = architecture,
                    numberOfDisplays = numberOfDisplays,
                    localeInfo = with(coreFeature.androidInfoProvider) {
                        LocaleInfo(
                            locales = locales,
                            currentLocale = currentLocale,
                            timeZone = timeZone
                        )
                    },
                    logicalCpuCount = logicalCpuCount,
                    totalRam = totalRam,
                    isLowRam = isLowRam
                )
            },
            userInfo = coreFeature.userInfoProvider.getUserInfo(),
            accountInfo = coreFeature.accountInfoProvider.getAccountInfo(),
            trackingConsent = coreFeature.trackingConsentProvider.getConsent(),
            appBuildId = coreFeature.appBuildId,
            featuresContext = mutableMapOf<String, Map<String, Any?>>().apply {
                withFeatureContexts.forEach {
                    val featureContext = featureContextProvider.getFeatureContext(it)
                    if (featureContext.isNotEmpty()) {
                        this[it] = featureContext
                    }
                }
            }
        )
    }
}
