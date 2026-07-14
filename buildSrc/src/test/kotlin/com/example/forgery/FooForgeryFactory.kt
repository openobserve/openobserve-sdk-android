/*
 * Unless explicitly stated otherwise all files in this repository are licensed under the Apache License Version 2.0.
 * This product includes software developed at OpenObserve (https://www.openobserve.ai/).
 * Copyright 2016-Present OpenObserve, Inc.
 */

package com.example.forgery

import com.example.model.Foo
import fr.xgouchet.elmyr.Forge
import fr.xgouchet.elmyr.ForgeryFactory

internal class FooForgeryFactory : ForgeryFactory<Foo> {

    override fun getForgery(forge: Forge): Foo {
        return Foo(
            bar = forge.aNullable { anAlphabeticalString() },
            baz = forge.aNullable { aLong() }
        )
    }
}
