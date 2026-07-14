/*
 * Unless explicitly stated otherwise all files in this repository are licensed under the Apache License Version 2.0.
 * This product includes software developed at OpenObserve (https://www.openobserve.ai/).
 * Copyright 2016-Present OpenObserve, Inc.
 */

package com.openobserve.android.rum.tracking

import fr.xgouchet.elmyr.Forge

internal data class StubComponentPredicate<T : Any>(
    val componentName: String,
    val name: String
) : ComponentPredicate<T> {

    constructor(forge: Forge, useAlpha: Boolean = true) :
        this(
            if (useAlpha) forge.anAlphabeticalString() else forge.aNumericalString(),
            if (useAlpha) forge.anAlphabeticalString() else forge.aNumericalString()
        )

    override fun accept(component: T): Boolean {
        return component.javaClass.simpleName == componentName
    }

    override fun getViewName(component: T): String? {
        return name
    }
}
