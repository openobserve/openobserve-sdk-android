/*
 * Unless explicitly stated otherwise all files in this repository are licensed under the Apache License Version 2.0.
 * This product includes software developed at OpenObserve (https://www.openobserve.ai/).
 * Copyright 2016-Present OpenObserve, Inc.
 */

package com.example.forgery

import com.example.model.DateTime
import fr.xgouchet.elmyr.Forge
import fr.xgouchet.elmyr.ForgeryFactory

internal class DateTimeForgeryFactory : ForgeryFactory<DateTime> {
    override fun getForgery(forge: Forge): DateTime {
        return DateTime(
            date = DateTime.Date(
                year = forge.aNullable { aLong() },
                month = forge.aNullable { getForgery() },
                day = forge.aNullable { aLong(1, 31) }
            ),
            time = DateTime.Time(
                hour = forge.aNullable { aLong(0, 24) },
                minute = forge.aNullable { aLong(0, 60) },
                seconds = forge.aNullable { aLong(0, 24) }
            )
        )
    }
}
