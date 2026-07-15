/*
 * Unless explicitly stated otherwise all files in this repository are licensed under the Apache License Version 2.0.
 * This product includes software developed at Datadog (https://www.datadoghq.com/).
 * Copyright 2016-Present Datadog, Inc.
 */

package com.openobserve.android.rum.internal.tracking

import android.app.Activity
import com.openobserve.android.api.SdkCore
import com.openobserve.tools.annotation.NoOpImplementation

@NoOpImplementation
internal interface FragmentLifecycleCallbacks<T : Activity> {

    // region Lifecycle

    fun register(activity: T, sdkCore: SdkCore)

    fun unregister(activity: T)

    // endregion
}
