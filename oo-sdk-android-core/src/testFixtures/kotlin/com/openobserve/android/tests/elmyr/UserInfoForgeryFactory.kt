/*
 * Unless explicitly stated otherwise all files in this repository are licensed under the Apache License Version 2.0.
 * This product includes software developed at OpenObserve (https://www.openobserve.ai/).
 * Copyright 2016-Present OpenObserve, Inc.
 */

package com.openobserve.android.tests.elmyr

import com.openobserve.android.api.context.UserInfo
import fr.xgouchet.elmyr.Forge
import fr.xgouchet.elmyr.ForgeryFactory

class UserInfoForgeryFactory : ForgeryFactory<UserInfo> {

    override fun getForgery(forge: Forge): UserInfo {
        return UserInfo(
            anonymousId = forge.aNullable() { anHexadecimalString() },
            id = forge.aNullable { anHexadecimalString() },
            name = forge.aNullable { forge.aStringMatching("[A-Z][a-z]+ [A-Z]\\. [A-Z][a-z]+") },
            email = forge.aNullable { forge.aStringMatching("[a-z]+\\.[a-z]+@[a-z]+\\.[a-z]{3}") },
            additionalProperties = forge.exhaustiveAttributes(excludedKeys = setOf("id", "name", "email"))
        )
    }
}
