/*
 * Unless explicitly stated otherwise all files in this repository are licensed under the Apache License Version 2.0.
 * This product includes software developed at Datadog (https://www.datadoghq.com/).
 * Copyright 2016-Present Datadog, Inc.
 */

package com.openobserve.android.core.internal.user

import com.openobserve.android.api.context.UserInfo
import com.openobserve.tools.annotation.NoOpImplementation

@NoOpImplementation
internal interface UserInfoProvider {

    fun getUserInfo(): UserInfo
}
