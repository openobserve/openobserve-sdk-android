/*
 * Unless explicitly stated otherwise all files in this repository are licensed under the Apache License Version 2.0.
 * This product includes software developed at OpenObserve (https://www.openobserve.ai/).
 * Copyright 2016-Present OpenObserve, Inc.
 */

package com.openobserve.android.rum.internal.startup

import android.app.Activity
import com.openobserve.android.rum.internal.domain.Time
import fr.xgouchet.elmyr.Forge
import java.lang.ref.WeakReference

internal fun Forge.testRumStartupScenarios(weakActivity: WeakReference<Activity>): List<RumStartupScenario> {
    val initialTimeNanos = aLong(min = 0, max = 1000000)
    val initialTime = Time(timestamp = initialTimeNanos / 1000000, nanoTime = initialTimeNanos)
    val hasSavedInstanceStateBundle = aBool()

    return listOf(
        RumStartupScenario.Cold(
            initialTime = initialTime,
            hasSavedInstanceStateBundle = hasSavedInstanceStateBundle,
            activity = weakActivity,
            appStartActivityOnCreateGapNs = aLong(min = 0, max = 10000)
        ),
        RumStartupScenario.WarmFirstActivity(
            initialTime = initialTime,
            hasSavedInstanceStateBundle = hasSavedInstanceStateBundle,
            activity = weakActivity,
            appStartActivityOnCreateGapNs = aLong(min = 0, max = 10000)
        ),
        RumStartupScenario.WarmAfterActivityDestroyed(
            initialTime = initialTime,
            hasSavedInstanceStateBundle = hasSavedInstanceStateBundle,
            activity = weakActivity
        )
    )
}
