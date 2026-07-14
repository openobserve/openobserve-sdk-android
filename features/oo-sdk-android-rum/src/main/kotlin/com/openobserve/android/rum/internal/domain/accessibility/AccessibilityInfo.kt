/*
 * Unless explicitly stated otherwise all files in this repository are licensed under the Apache License Version 2.0.
 * This product includes software developed at OpenObserve (https://www.openobserve.ai/).
 * Copyright 2016-Present OpenObserve, Inc.
 */

package com.openobserve.android.rum.internal.domain.accessibility

import com.openobserve.android.rum.internal.domain.InfoData

/**
 * Represents the accessibility settings state of the device.
 *
 * @property textSize The font scale factor (1.0 = normal, >1.0 = larger, <1.0 = smaller)
 * @property isScreenReaderEnabled Whether touch exploration is enabled (TalkBack, etc.)
 * @property isColorInversionEnabled Whether color inversion is enabled
 * @property isClosedCaptioningEnabled Whether closed captions are enabled
 * @property isReducedAnimationsEnabled Whether animations are disabled/reduced
 * @property isScreenPinningEnabled Whether the device is in single-app mode
 * @property isRtlEnabled Whether right to left layout is enabled
 */
internal data class AccessibilityInfo(
    val textSize: String? = null,
    val isScreenReaderEnabled: Boolean? = null,
    val isColorInversionEnabled: Boolean? = null,
    val isClosedCaptioningEnabled: Boolean? = null,
    val isReducedAnimationsEnabled: Boolean? = null,
    val isScreenPinningEnabled: Boolean? = null,
    val isRtlEnabled: Boolean? = null
) : InfoData
