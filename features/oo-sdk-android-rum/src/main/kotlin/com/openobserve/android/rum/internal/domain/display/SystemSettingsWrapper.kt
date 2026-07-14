/*
 * Unless explicitly stated otherwise all files in this repository are licensed under the Apache License Version 2.0.
 * This product includes software developed at OpenObserve (https://www.openobserve.ai/).
 * Copyright 2016-Present OpenObserve, Inc.
 */

package com.openobserve.android.rum.internal.domain.display

import android.content.Context
import android.provider.Settings
import com.openobserve.android.api.InternalLogger

internal class SystemSettingsWrapper(
    private val applicationContext: Context,
    private val internalLogger: InternalLogger
) {
    fun getInt(name: String): Int {
        return try {
            Settings.System.getInt(applicationContext.contentResolver, name)
        } catch (e: Settings.SettingNotFoundException) {
            internalLogger.log(
                target = InternalLogger.Target.MAINTAINER,
                level = InternalLogger.Level.WARN,
                messageBuilder = { "Problem retrieving system value for $name" },
                throwable = e
            )
            Integer.MIN_VALUE
        }
    }
}
