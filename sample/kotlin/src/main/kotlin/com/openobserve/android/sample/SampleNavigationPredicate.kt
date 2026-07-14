/*
 * Unless explicitly stated otherwise all files in this repository are licensed under the Apache License Version 2.0.
 * This product includes software developed at OpenObserve (https://www.openobserve.ai/).
 * Copyright 2016-Present OpenObserve, Inc.
 */

package com.openobserve.android.sample

import androidx.navigation.NavDestination
import com.openobserve.android.rum.tracking.ComponentPredicate

internal class SampleNavigationPredicate : ComponentPredicate<NavDestination> {
    override fun accept(component: NavDestination): Boolean {
        return true
    }

    override fun getViewName(component: NavDestination): String {
        return component.label.toString()
    }
}
