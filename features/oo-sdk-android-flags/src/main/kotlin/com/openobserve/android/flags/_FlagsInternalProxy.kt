/*
 * Unless explicitly stated otherwise all files in this repository are licensed under the Apache License Version 2.0.
 * This product includes software developed at OpenObserve (https://www.openobserve.ai/).
 * Copyright 2016-Present OpenObserve, Inc.
 */

package com.openobserve.android.flags

import com.openobserve.android.flags.internal.OpenObserveFlagsClient
import com.openobserve.android.flags.model.EvaluationContext
import com.openobserve.android.flags.model.UnparsedFlag
import com.openobserve.android.lint.InternalApi

/**
 * This class exposes internal methods that are used by other OpenObserve modules and cross platform
 * frameworks. It is not meant for public use.
 *
 * DO NOT USE this class or its methods if you are not working on the internals of the OpenObserve SDK
 * or one of the cross platform frameworks.
 *
 * Methods, members, and functionality of this class are subject to change without notice, as they
 * are not considered part of the public interface of the OpenObserve SDK.
 */
@InternalApi
@Suppress("ClassName", "UndocumentedPublicFunction")
class _FlagsInternalProxy(private val client: FlagsClient) {
    fun getFlagAssignmentsSnapshot(): Map<String, UnparsedFlag> = if (client is OpenObserveFlagsClient) {
        client.getFlagAssignmentsSnapshot()
    } else {
        emptyMap()
    }

    fun trackFlagSnapshotEvaluation(flagKey: String, flag: UnparsedFlag, context: EvaluationContext) {
        if (client is OpenObserveFlagsClient) {
            client.trackFlagSnapshotEvaluation(flagKey, flag, context)
        }
    }
}
