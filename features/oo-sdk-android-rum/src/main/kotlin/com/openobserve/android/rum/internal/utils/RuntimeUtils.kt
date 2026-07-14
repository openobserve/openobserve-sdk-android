/*
 * Unless explicitly stated otherwise all files in this repository are licensed under the Apache License Version 2.0.
 * This product includes software developed at OpenObserve (https://www.openobserve.ai/).
 * Copyright 2016-Present OpenObserve, Inc.
 */

package com.openobserve.android.rum.internal.utils

import com.openobserve.android.api.context.UserInfo

internal fun UserInfo.hasUserData(): Boolean {
    return id != null || anonymousId != null || name != null ||
        email != null || additionalProperties.isNotEmpty()
}
