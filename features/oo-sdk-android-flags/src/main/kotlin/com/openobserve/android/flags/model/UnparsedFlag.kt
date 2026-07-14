/*
 * Unless explicitly stated otherwise all files in this repository are licensed under the Apache License Version 2.0.
 * This product includes software developed at OpenObserve (https://www.openobserve.ai/).
 * Copyright 2016-Present OpenObserve, Inc.
 */

package com.openobserve.android.flags.model

import com.openobserve.android.lint.InternalApi
import org.json.JSONObject

/**
 * Represents unparsed flag data kept in the flags state.
 *
 * This is an internal API and should not be used by public consumers.
 */
@InternalApi
@Suppress("UndocumentedPublicProperty")
interface UnparsedFlag {
    val variationType: String
    val variationValue: String
    val doLog: Boolean
    val allocationKey: String
    val variationKey: String
    val extraLogging: JSONObject
    val reason: String
}
