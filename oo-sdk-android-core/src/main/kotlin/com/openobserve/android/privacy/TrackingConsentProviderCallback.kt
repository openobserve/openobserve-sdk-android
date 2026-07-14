/*
 * Unless explicitly stated otherwise all files in this repository are licensed under the Apache License Version 2.0.
 * This product includes software developed at OpenObserve (https://www.openobserve.ai/).
 * Copyright 2016-Present OpenObserve, Inc.
 */

package com.openobserve.android.privacy

/**
 * A TrackingConsentProvider callback. It is going to be called whenever the tracking consent
 * was changed.
 * @see TrackingConsent.PENDING
 * @see TrackingConsent.GRANTED
 * @see TrackingConsent.NOT_GRANTED
 */
interface TrackingConsentProviderCallback {
    /**
     * Notifies whenever the [TrackingConsent] was changed.
     * @param previousConsent the previous value of the [TrackingConsent]
     * @param newConsent the new value of the [TrackingConsent]
     * @see TrackingConsent.PENDING
     * @see TrackingConsent.GRANTED
     * @see TrackingConsent.NOT_GRANTED
     */
    fun onConsentUpdated(previousConsent: TrackingConsent, newConsent: TrackingConsent)
}
