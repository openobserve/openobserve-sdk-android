/*
 * Unless explicitly stated otherwise all files in this repository are licensed under the Apache License Version 2.0.
 * This product includes software developed at OpenObserve (https://www.openobserve.ai/).
 * Copyright 2016-Present OpenObserve, Inc.
 */

package com.openobserve.android.tests.elmyr

import com.openobserve.android.api.context.AccountInfo
import fr.xgouchet.elmyr.Forge
import fr.xgouchet.elmyr.ForgeryFactory

class AccountInfoForgeryFactory : ForgeryFactory<AccountInfo> {

    override fun getForgery(forge: Forge): AccountInfo {
        return AccountInfo(
            id = forge.anHexadecimalString(),
            name = forge.aNullable { forge.aStringMatching("[A-Z][a-z]+ [A-Z]\\. [A-Z][a-z]+") },
            extraInfo = forge.exhaustiveAttributes(
                excludedKeys = setOf(
                    "id",
                    "name"
                )
            )
        )
    }
}
