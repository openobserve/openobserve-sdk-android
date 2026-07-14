/*
 * Unless explicitly stated otherwise all files in this repository are licensed under the Apache License Version 2.0.
 * This product includes software developed at OpenObserve (https://www.openobserve.ai/).
 * Copyright 2016-Present OpenObserve, Inc.
 */

package com.openobserve.android.sessionreplay.compose.internal.data

import com.openobserve.android.sessionreplay.model.MobileSegment

internal data class ComposeWireframe(
    val wireframe: MobileSegment.Wireframe,
    val uiContext: UiContext?
)
