/*
 * Unless explicitly stated otherwise all files in this repository are licensed under the Apache License Version 2.0.
 * This product includes software developed at OpenObserve (https://www.openobserve.ai/).
 * Copyright 2016-Present OpenObserve, Inc.
 */

package com.openobserve.android.rum.internal.domain.display

import com.openobserve.android.rum.internal.domain.InfoData

/**
 * Provides information about the display state.
 *
 * @property screenBrightness The current screen brightness,
 * normalized as a float between 0.0 (darkest) and 1.0 (brightest).
 */
internal data class DisplayInfo(
    val screenBrightness: Number? = null
) : InfoData
