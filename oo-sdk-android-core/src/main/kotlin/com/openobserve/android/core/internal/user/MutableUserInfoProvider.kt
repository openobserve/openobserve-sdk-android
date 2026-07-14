/*
 * Unless explicitly stated otherwise all files in this repository are licensed under the Apache License Version 2.0.
 * This product includes software developed at OpenObserve (https://www.openobserve.ai/).
 * Copyright 2016-Present OpenObserve, Inc.
 */

package com.openobserve.android.core.internal.user

import com.openobserve.tools.annotation.NoOpImplementation

@NoOpImplementation
internal interface MutableUserInfoProvider : UserInfoProvider {

    fun setUserInfo(
        id: String,
        name: String?,
        email: String?,
        extraInfo: Map<String, Any?>
    )

    fun setAnonymousId(id: String?)

    fun addUserProperties(properties: Map<String, Any?>)

    fun clearUserInfo()
}
