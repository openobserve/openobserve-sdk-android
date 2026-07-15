/*
 * Unless explicitly stated otherwise all files in this repository are licensed under the Apache License Version 2.0.
 * This product includes software developed at Datadog (https://www.datadoghq.com/).
 * Copyright 2016-Present Datadog, Inc.
 */

package com.openobserve.tools.detekt

import com.openobserve.tools.detekt.rules.sdk.CheckInternal
import com.openobserve.tools.detekt.rules.sdk.InvalidStringFormat
import com.openobserve.tools.detekt.rules.sdk.PackageNameVisibility
import com.openobserve.tools.detekt.rules.sdk.PreferTimeProvider
import com.openobserve.tools.detekt.rules.sdk.RequireInternal
import com.openobserve.tools.detekt.rules.sdk.ThreadSafety
import com.openobserve.tools.detekt.rules.sdk.ThrowingInternalException
import com.openobserve.tools.detekt.rules.sdk.TodoWithoutTask
import com.openobserve.tools.detekt.rules.sdk.UnsafeCallOnNullableType
import com.openobserve.tools.detekt.rules.sdk.UnsafeThirdPartyFunctionCall
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.RuleSet
import io.gitlab.arturbosch.detekt.api.RuleSetProvider

/**
 * The [RuleSetProvider] for OpenObserve's SDK for Android.
 */
class OpenObserveProvider : RuleSetProvider {

    override val ruleSetId: String = "datadog"

    override fun instance(config: Config): RuleSet {
        return RuleSet(
            ruleSetId,
            listOf(
                CheckInternal(),
                InvalidStringFormat(),
                PackageNameVisibility(config),
                PreferTimeProvider(config),
                RequireInternal(),
                ThreadSafety(config),
                ThrowingInternalException(),
                TodoWithoutTask(config),
                UnsafeCallOnNullableType(),
                UnsafeThirdPartyFunctionCall(config)
            )
        )
    }
}
