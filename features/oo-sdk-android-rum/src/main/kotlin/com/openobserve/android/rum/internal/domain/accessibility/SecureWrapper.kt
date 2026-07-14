/*
 * Unless explicitly stated otherwise all files in this repository are licensed under the Apache License Version 2.0.
 * This product includes software developed at OpenObserve (https://www.openobserve.ai/).
 * Copyright 2016-Present OpenObserve, Inc.
 */

package com.openobserve.android.rum.internal.domain.accessibility

import android.content.Context
import android.provider.Settings
import android.provider.Settings.SettingNotFoundException
import com.openobserve.android.api.InternalLogger

internal class SecureWrapper {
    @Suppress("UnsafeThirdPartyFunctionCall")
    internal fun getInt(
        internalLogger: InternalLogger,
        applicationContext: Context,
        key: String
    ): Int? {
        // returns -1 if unable to retrieve the key
        return try {
            Settings.Secure.getInt(
                applicationContext.contentResolver,
                key,
                -1
            )
        } catch (e: SettingNotFoundException) {
            internalLogger.log(
                InternalLogger.Level.ERROR,
                listOf(InternalLogger.Target.MAINTAINER),
                { "Setting cannot be found $key" },
                e
            )
            -1
        } catch (e: SecurityException) {
            internalLogger.log(
                InternalLogger.Level.ERROR,
                listOf(InternalLogger.Target.MAINTAINER),
                { "Security exception accessing $key" },
                e
            )
            -1
        }
    }
}
