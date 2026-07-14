/*
 * Unless explicitly stated otherwise all files in this repository are licensed under the Apache License Version 2.0.
 * This product includes software developed at OpenObserve (https://www.openobserve.ai/).
 * Copyright 2016-Present OpenObserve, Inc.
 */

package com.openobserve.android.flags.internal.model

import com.openobserve.android.flags.model.UnparsedFlag
import org.json.JSONObject

internal data class PrecomputedFlag(
    override val variationType: String,
    override val variationValue: String,
    override val doLog: Boolean,
    override val allocationKey: String,
    override val variationKey: String,
    override val extraLogging: JSONObject,
    override val reason: String
) : UnparsedFlag
