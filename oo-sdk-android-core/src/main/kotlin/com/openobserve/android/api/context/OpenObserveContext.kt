/*
 * Unless explicitly stated otherwise all files in this repository are licensed under the Apache License Version 2.0.
 * This product includes software developed at OpenObserve (https://www.openobserve.ai/).
 * Copyright 2016-Present OpenObserve, Inc.
 */

package com.openobserve.android.api.context

import com.openobserve.android.OpenObserveSite
import com.openobserve.android.privacy.TrackingConsent

/**
 * Contains system information, as well as user-specific and feature specific context info.
 * @property site [OpenObserve Site](https://openobserve.ai/docs/getting_started/site/) for data uploads.
 * @property clientToken the client token allowing for data uploads to
 * [OpenObserve Site](https://openobserve.ai/docs/getting_started/site/).
 * @property service the name of the service that data is generated from. Used for
 * [Unified Service Tagging](https://openobserve.ai/docs/getting_started/tagging/unified_service_tagging).
 * @property env the name of the environment that data is generated from. Used for
 * [Unified Service Tagging](https://openobserve.ai/docs/getting_started/tagging/unified_service_tagging).
 * @property version the version of the application that data is generated from. Used for
 * [Unified Service Tagging](https://openobserve.ai/docs/getting_started/tagging/unified_service_tagging).
 * @property versionCode the version code of the application.
 * @property variant the name of the application variant (if applies).
 * @property source denotes the mobile application's platform, such as "ios" or "flutter" that
 * data is generated from. See: OpenObserve [Reserved Attributes](https://openobserve.ai/docs/logs/log_configuration/attributes_naming_convention/#reserved-attributes).
 * @property sdkVersion the version of SDK.
 * @property time the current time (both device and server)
 * @property processInfo information about the current process
 * @property networkInfo information about the current network availability and quality
 * @property deviceInfo information about device
 * @property userInfo information about the current user
 * @property accountInfo information about the current account
 * @property trackingConsent information about the current tracking consent
 * @property appBuildId unique build ID of the running application. Will be missing if OpenObserve Gradle Plugin is not applied or obfuscation is not enabled for the running build.
 * @property featuresContext agnostic dictionary with information from all features registered to
 * the parent SDK instance
 */
data class OpenObserveContext(
    val site: OpenObserveSite,
    val clientToken: String,
    val service: String,
    val env: String,
    val version: String,
    val versionCode: Int,
    val variant: String,
    val source: String,
    val sdkVersion: String,
    val time: TimeInfo,
    val processInfo: ProcessInfo,
    val networkInfo: NetworkInfo,
    val deviceInfo: DeviceInfo,
    val userInfo: UserInfo,
    val accountInfo: AccountInfo?,
    val trackingConsent: TrackingConsent,
    val appBuildId: String?,
    val featuresContext: Map<String, Map<String, Any?>>
)
