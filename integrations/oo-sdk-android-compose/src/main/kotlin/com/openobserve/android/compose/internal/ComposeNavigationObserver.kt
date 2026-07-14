/*
 * Unless explicitly stated otherwise all files in this repository are licensed under the Apache License Version 2.0.
 * This product includes software developed at OpenObserve (https://www.openobserve.ai/).
 * Copyright 2016-Present OpenObserve, Inc.
 */

package com.openobserve.android.compose.internal

import android.os.Bundle
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import com.openobserve.android.internal.attributes.ViewScopeInstrumentationType
import com.openobserve.android.internal.attributes.enrichWithConstantAttribute
import com.openobserve.android.rum.RumMonitor
import com.openobserve.android.rum.tracking.AcceptAllNavDestinations
import com.openobserve.android.rum.tracking.ComponentPredicate
import com.openobserve.android.rum.tracking.convertToRumViewAttributes

internal class ComposeNavigationObserver(
    private val trackArguments: Boolean = true,
    private val destinationPredicate: ComponentPredicate<NavDestination> =
        AcceptAllNavDestinations(),
    private val navController: NavController,
    private val rumMonitor: RumMonitor
) : LifecycleEventObserver, NavController.OnDestinationChangedListener {

    override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
        if (event == Lifecycle.Event.ON_RESUME) {
            // once listener is added, it will receive current destination if available
            navController.addOnDestinationChangedListener(this)
        } else if (event == Lifecycle.Event.ON_PAUSE) {
            navController.currentDestination?.route?.let {
                rumMonitor.stopView(it)
            }

            navController.removeOnDestinationChangedListener(this)
        }
    }

    override fun onDestinationChanged(
        controller: NavController,
        destination: NavDestination,
        arguments: Bundle?
    ) {
        if (destinationPredicate.accept(destination)) {
            destination.route?.let {
                startView(destination, it, arguments)
            }
        }
    }

    internal fun onDispose() {
        // just a safe-guard if ON_PAUSE wasn't called
        navController.removeOnDestinationChangedListener(this)
    }

    private fun startView(
        navDestination: NavDestination,
        route: String,
        arguments: Bundle?
    ) {
        val viewName = destinationPredicate.getViewName(navDestination) ?: route
        val attributes = if (trackArguments) {
            arguments.convertToRumViewAttributes().toMutableMap()
        } else {
            mutableMapOf()
        }.enrichWithConstantAttribute(ViewScopeInstrumentationType.Native.COMPOSE)

        rumMonitor.startView(key = route, name = viewName, attributes = attributes)
    }
}
