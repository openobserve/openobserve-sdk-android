/*
 * Unless explicitly stated otherwise all files in this repository are licensed under the Apache License Version 2.0.
 * This product includes software developed at OpenObserve (https://www.openobserve.ai/).
 * Copyright 2016-Present OpenObserve, Inc.
 */
package com.openobserve.android.cronet.internal

import com.openobserve.android.api.instrumentation.network.HttpRequestBody
import org.chromium.net.UploadDataProvider
import java.util.concurrent.Executor

internal data class CronetHttpRequestBody(
    val uploadProvider: UploadDataProvider,
    val executor: Executor
) : HttpRequestBody
