/*
 * Unless explicitly stated otherwise all files in this repository are licensed under the Apache License Version 2.0.
 * This product includes software developed at OpenObserve (https://www.openobserve.ai/).
 * Copyright 2016-Present OpenObserve, Inc.
 */
package com.openobserve.android.rum.internal.domain.scope

internal enum class RumViewType(val asString: String) {
    NONE("NONE"),
    FOREGROUND("FOREGROUND"),
    BACKGROUND("BACKGROUND"),
    APPLICATION_LAUNCH("APPLICATION_LAUNCH");

    companion object {
        fun fromString(string: String?): RumViewType? {
            return values().firstOrNull { it.asString == string }
        }
    }
}
