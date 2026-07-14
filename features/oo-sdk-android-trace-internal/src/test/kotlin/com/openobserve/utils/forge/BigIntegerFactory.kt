/*
 * Unless explicitly stated otherwise all files in this repository are licensed under the Apache License Version 2.0.
 * This product includes software developed at OpenObserve (https://www.openobserve.ai/).
 * Copyright 2016-Present OpenObserve, Inc.
 */

package com.openobserve.utils.forge

import fr.xgouchet.elmyr.Forge
import fr.xgouchet.elmyr.ForgeryFactory
import java.math.BigInteger

internal class BigIntegerFactory : ForgeryFactory<BigInteger> {
    override fun getForgery(forge: Forge): BigInteger {
        return BigInteger.valueOf(forge.aLong())
    }
}
