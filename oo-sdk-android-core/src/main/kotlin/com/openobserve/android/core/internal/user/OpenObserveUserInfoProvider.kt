/*
 * Unless explicitly stated otherwise all files in this repository are licensed under the Apache License Version 2.0.
 * This product includes software developed at OpenObserve (https://www.openobserve.ai/).
 * Copyright 2016-Present OpenObserve, Inc.
 */

package com.openobserve.android.core.internal.user

import com.openobserve.android.api.context.UserInfo

internal class OpenObserveUserInfoProvider : MutableUserInfoProvider {

    @Volatile
    private var internalUserInfo = UserInfo()

    override fun setUserInfo(id: String, name: String?, email: String?, extraInfo: Map<String, Any?>) {
        internalUserInfo = internalUserInfo.copy(
            id = id,
            name = name,
            email = email,
            additionalProperties = extraInfo.toMap()
        )
    }

    override fun setAnonymousId(id: String?) {
        internalUserInfo = internalUserInfo.copy(
            anonymousId = id
        )
    }

    override fun addUserProperties(properties: Map<String, Any?>) {
        internalUserInfo = internalUserInfo.copy(
            additionalProperties = internalUserInfo.additionalProperties + properties
        )
    }

    override fun clearUserInfo() {
        internalUserInfo = UserInfo(internalUserInfo.anonymousId)
    }

    override fun getUserInfo(): UserInfo {
        return internalUserInfo
    }
}
