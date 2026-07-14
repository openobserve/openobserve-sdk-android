/*
 * Unless explicitly stated otherwise all files in this repository are licensed under the Apache License Version 2.0.
 * This product includes software developed at OpenObserve (https://www.openobserve.ai/).
 * Copyright 2016-Present OpenObserve, Inc.
 */

package com.openobserve.android.apollo.internal

import com.apollographql.apollo.api.CustomScalarAdapters
import com.apollographql.apollo.api.Operation
import com.apollographql.apollo.api.variablesJson
import okio.IOException

/**
 * Default implementation of [VariablesExtractor] that uses Apollo's built-in variable extraction.
 */
internal class DefaultVariablesExtractor : VariablesExtractor {
    override fun extractVariables(operation: Operation<*>, adapters: CustomScalarAdapters): String? {
        return try {
            operation.variablesJson(adapters)
        } catch (_: IOException) {
            null
        }
    }
}
