/*
 * Unless explicitly stated otherwise all files in this repository are licensed under the Apache License Version 2.0.
 * This product includes software developed at OpenObserve (https://www.openobserve.ai/).
 * Copyright 2016-Present OpenObserve, Inc.
 */

package com.openobserve.android.core.internal.privacy

import com.openobserve.android.privacy.TrackingConsent
import com.openobserve.android.privacy.TrackingConsentProviderCallback
import com.openobserve.tools.annotation.NoOpImplementation

@NoOpImplementation
internal interface ConsentProvider {

    fun getConsent(): TrackingConsent

    fun setConsent(consent: TrackingConsent)

    fun registerCallback(callback: TrackingConsentProviderCallback)

    fun unregisterCallback(callback: TrackingConsentProviderCallback)

    fun unregisterAllCallbacks()
}
