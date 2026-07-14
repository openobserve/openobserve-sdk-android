/*
 * Unless explicitly stated otherwise all files in this repository are licensed under the Apache License Version 2.0.
 * This product includes software developed at OpenObserve (https://www.openobserve.ai/).
 * Copyright 2016-Present OpenObserve, Inc.
 */

package com.openobserve.android.core.internal.privacy

import com.openobserve.android.privacy.TrackingConsent
import com.openobserve.android.privacy.TrackingConsentProviderCallback
import java.util.LinkedList

internal class TrackingConsentProvider(consent: TrackingConsent) :
    ConsentProvider {

    private val callbacks: LinkedList<TrackingConsentProviderCallback> = LinkedList()

    @Volatile
    private var consent: TrackingConsent

    // region ConsentProvider

    init {
        this.consent = consent
    }

    override fun getConsent(): TrackingConsent {
        return consent
    }

    // We need to synchronize everything as we are not sure from which thread the client
    // will update the consent and initialize the SDK.
    @Synchronized
    override fun setConsent(consent: TrackingConsent) {
        if (consent == this.consent) {
            return
        }
        val previous = this.consent
        this.consent = consent
        notifyCallbacks(previous, consent)
    }

    @Synchronized
    override fun registerCallback(callback: TrackingConsentProviderCallback) {
        callbacks.add(callback)
    }

    @Synchronized
    override fun unregisterCallback(callback: TrackingConsentProviderCallback) {
        callbacks.remove(callback)
    }

    @Synchronized
    override fun unregisterAllCallbacks() {
        callbacks.clear()
    }

    // endregion

    // region Internal

    private fun notifyCallbacks(previous: TrackingConsent, new: TrackingConsent) {
        callbacks.forEach {
            it.onConsentUpdated(previous, new)
        }
    }

    // endregion
}
