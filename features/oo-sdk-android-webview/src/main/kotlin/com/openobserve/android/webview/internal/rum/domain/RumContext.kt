/*
 * Unless explicitly stated otherwise all files in this repository are licensed under the Apache License Version 2.0.
 * This product includes software developed at OpenObserve (https://www.openobserve.ai/).
 * Copyright 2016-Present OpenObserve, Inc.
 */

package com.openobserve.android.webview.internal.rum.domain

import java.util.UUID

internal data class RumContext(
    val applicationId: String,
    val sessionId: String,
    val sessionState: String
) {

    companion object {
        val NULL_UUID = UUID(0, 0).toString()
    }
}
