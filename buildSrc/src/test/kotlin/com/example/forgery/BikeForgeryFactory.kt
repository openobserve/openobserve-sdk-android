/*
 * Unless explicitly stated otherwise all files in this repository are licensed under the Apache License Version 2.0.
 * This product includes software developed at OpenObserve (https://www.openobserve.ai/).
 * Copyright 2016-Present OpenObserve, Inc.
 */

package com.example.forgery

import com.example.model.Bike
import fr.xgouchet.elmyr.Forge
import fr.xgouchet.elmyr.ForgeryFactory

internal class BikeForgeryFactory : ForgeryFactory<Bike> {
    override fun getForgery(forge: Forge): Bike {
        return Bike(
            productId = forge.aLong(),
            productName = forge.aString(),
            type = forge.aNullable { aString() },
            price = forge.aNumber(),
            frameMaterial = forge.aNullable { aValueFrom(Bike.FrameMaterial::class.java) },
            inStock = forge.aBool(),
            color = forge.aValueFrom(Bike.Color::class.java)
        )
    }
}
