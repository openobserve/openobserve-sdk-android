/*
 * Unless explicitly stated otherwise all files in this repository are licensed under the Apache License Version 2.0.
 * This product includes software developed at OpenObserve (https://www.openobserve.ai/).
 * Copyright 2016-Present OpenObserve, Inc.
 */

package com.openobserve.android.sdk.utils

import fr.xgouchet.elmyr.Forge

fun Forge.exhaustiveAttributes(): Map<String, Any?> {
    return listOf(
        aBool(),
        anInt(),
        aLong(),
        aFloat(),
        aDouble(),
        anAsciiString(),
        aList { anAlphabeticalString() },
        null
    ).associateBy { anAlphabeticalString() }
}
