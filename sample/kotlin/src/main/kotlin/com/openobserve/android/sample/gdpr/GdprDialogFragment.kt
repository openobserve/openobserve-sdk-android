/*
 * Unless explicitly stated otherwise all files in this repository are licensed under the Apache License Version 2.0.
 * This product includes software developed at OpenObserve (https://www.openobserve.ai/).
 * Copyright 2016-Present OpenObserve, Inc.
 */

package com.openobserve.android.sample.gdpr

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioGroup
import androidx.annotation.IdRes
import androidx.fragment.app.DialogFragment
import com.openobserve.android.OpenObserve
import com.openobserve.android.privacy.TrackingConsent
import com.openobserve.android.sample.Preferences
import com.openobserve.android.sample.R
import com.openobserve.android.sample.TrackingConsentChangeListener

internal class GdprDialogFragment : DialogFragment() {
    lateinit var trackingConsentSelector: RadioGroup
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.fragment_gdpr, container, false)
        trackingConsentSelector = rootView.findViewById(R.id.tracking_consent_selector)
        @Suppress("CheckInternal") // not a Kotlin check
        trackingConsentSelector.check(
            resolveButtonIdFromConsent(
                Preferences.defaultPreferences(requireContext()).getTrackingConsent()
            )
        )
        trackingConsentSelector.setOnCheckedChangeListener { _, checkedId ->
            val trackingConsent = when (checkedId) {
                R.id.pending -> TrackingConsent.PENDING
                R.id.granted -> TrackingConsent.GRANTED
                else -> TrackingConsent.NOT_GRANTED
            }
            OpenObserve.setTrackingConsent(trackingConsent)
            Preferences.defaultPreferences(requireContext()).setTrackingConsent(trackingConsent)
            (activity as? TrackingConsentChangeListener)?.onTrackingConsentChanged(trackingConsent)
        }
        return rootView
    }

    @IdRes
    private fun resolveButtonIdFromConsent(trackingConsent: TrackingConsent): Int {
        return when (trackingConsent) {
            TrackingConsent.PENDING -> R.id.pending
            TrackingConsent.GRANTED -> R.id.granted
            TrackingConsent.NOT_GRANTED -> R.id.not_granted
        }
    }
}
