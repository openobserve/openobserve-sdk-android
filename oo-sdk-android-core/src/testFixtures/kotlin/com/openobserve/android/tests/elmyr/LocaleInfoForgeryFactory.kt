/*
 * Unless explicitly stated otherwise all files in this repository are licensed under the Apache License Version 2.0.
 * This product includes software developed at OpenObserve (https://www.openobserve.ai/).
 * Copyright 2016-Present OpenObserve, Inc.
 */

package com.openobserve.android.tests.elmyr

import com.openobserve.android.api.context.LocaleInfo
import fr.xgouchet.elmyr.Forge
import fr.xgouchet.elmyr.ForgeryFactory

class LocaleInfoForgeryFactory : ForgeryFactory<LocaleInfo> {

    override fun getForgery(forge: Forge): LocaleInfo {
        return LocaleInfo(
            locales = forge.aList { forge.aString() },
            currentLocale = forge.aString(),
            timeZone = forge.aString()
        )
    }
}
