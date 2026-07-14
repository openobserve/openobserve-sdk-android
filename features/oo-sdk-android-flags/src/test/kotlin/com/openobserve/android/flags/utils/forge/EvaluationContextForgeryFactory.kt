/*
 * Unless explicitly stated otherwise all files in this repository are licensed under the Apache License Version 2.0.
 * This product includes software developed at OpenObserve (https://www.openobserve.ai/).
 * Copyright 2016-Present OpenObserve, Inc.
 */

package com.openobserve.android.flags.utils.forge

import com.openobserve.android.flags.model.EvaluationContext
import fr.xgouchet.elmyr.Forge
import fr.xgouchet.elmyr.ForgeryFactory

internal class EvaluationContextForgeryFactory : ForgeryFactory<EvaluationContext> {
    override fun getForgery(forge: Forge): EvaluationContext {
        val targetingKey = forge.anAlphabeticalString()
        val attributeCount = forge.anInt(min = 0, max = 5)
        val attributes = mutableMapOf<String, String>()

        repeat(attributeCount) {
            attributes[forge.anAlphabeticalString()] = forge.anAlphabeticalString()
        }

        return EvaluationContext(
            targetingKey = targetingKey,
            attributes = attributes
        )
    }
}
