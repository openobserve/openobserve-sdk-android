/*
 * Unless explicitly stated otherwise all files in this repository are licensed under the Apache License Version 2.0.
 * This product includes software developed at OpenObserve (https://www.openobserve.ai/).
 * Copyright 2016-Present OpenObserve, Inc.
 */

package com.openobserve.tools.unit.forge

import fr.xgouchet.elmyr.Forge
import fr.xgouchet.elmyr.ForgeryFactory

/**
 *  A [ForgeryFactory] generating a random [Error] instance with a forged message.
 */
class ErrorForgeryFactory :
    ForgeryFactory<Error> {
    /** @inheritDoc */
    override fun getForgery(forge: Forge): Error {
        return forge.anError()
    }
}
