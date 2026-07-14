/*
 * Unless explicitly stated otherwise all files in this repository are licensed under the Apache License Version 2.0.
 * This product includes software developed at OpenObserve (https://www.openobserve.ai/).
 * Copyright 2016-Present OpenObserve, Inc.
 */

package com.openobserve.android.rum.internal.utils

import com.openobserve.android.api.context.OpenObserveContext
import com.openobserve.android.rum.RumAttributes

internal fun buildDDTagsString(context: OpenObserveContext): String {
    return buildString {
        with(context) {
            append("${RumAttributes.SERVICE_NAME}:$service").append(",")
            append("${RumAttributes.APPLICATION_VERSION}:$version").append(",")
            append("${RumAttributes.SDK_VERSION}:$sdkVersion").append(",")
            append("${RumAttributes.ENV}:$env")

            if (variant.isNotEmpty()) {
                append(",").append("${RumAttributes.VARIANT}:$variant")
            }
        }
    }
}
