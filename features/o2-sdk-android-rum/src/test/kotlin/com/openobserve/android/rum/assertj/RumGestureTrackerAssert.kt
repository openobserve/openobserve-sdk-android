/*
 * Unless explicitly stated otherwise all files in this repository are licensed under the Apache License Version 2.0.
 * This product includes software developed at Datadog (https://www.datadoghq.com/).
 * Copyright 2016-Present Datadog, Inc.
 */

package com.openobserve.android.rum.assertj

import com.openobserve.android.rum.internal.instrumentation.gestures.OpenObserveGesturesTracker
import com.openobserve.android.rum.internal.tracking.JetpackViewAttributesProvider
import com.openobserve.android.rum.tracking.InteractionPredicate
import com.openobserve.android.rum.tracking.ViewAttributesProvider
import org.assertj.core.api.AbstractObjectAssert
import org.assertj.core.api.Assertions.assertThat

internal class RumGestureTrackerAssert(actual: OpenObserveGesturesTracker) :
    AbstractObjectAssert<RumGestureTrackerAssert, OpenObserveGesturesTracker>(
        actual,
        RumGestureTrackerAssert::class.java
    ) {

    // region Assertions

    fun hasDefaultTargetAttributesProviders(): RumGestureTrackerAssert {
        val count = actual.targetAttributesProviders
            .filterIsInstance<JetpackViewAttributesProvider>()
            .count()
        assertThat(count)
            .overridingErrorMessage(
                "We were expecting" +
                    " a JetpackViewAttributesProvider as a default provider" +
                    " but we could not find any"
            )
            .isEqualTo(1)
        return this
    }

    fun hasCustomTargetAttributesProviders(customProviders: Array<ViewAttributesProvider>): RumGestureTrackerAssert {
        assertThat(actual.targetAttributesProviders)
            .containsAll(customProviders.toMutableList())
        return this
    }

    fun hasInteractionPredicate(interactionPredicate: InteractionPredicate): RumGestureTrackerAssert {
        assertThat(actual.interactionPredicate).isEqualTo(interactionPredicate)
        return this
    }

    fun hasInteractionPredicateOfType(type: Class<*>): RumGestureTrackerAssert {
        assertThat(actual.interactionPredicate).isInstanceOf(type)
        return this
    }

    // endregion

    companion object {

        internal fun assertThat(actual: OpenObserveGesturesTracker): RumGestureTrackerAssert =
            RumGestureTrackerAssert(actual)
    }
}
