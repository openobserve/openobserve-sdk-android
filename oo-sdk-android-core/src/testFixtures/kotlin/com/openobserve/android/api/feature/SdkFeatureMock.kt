/*
 * Unless explicitly stated otherwise all files in this repository are licensed under the Apache License Version 2.0.
 * This product includes software developed at OpenObserve (https://www.openobserve.ai/).
 * Copyright 2016-Present OpenObserve, Inc.
 */

package com.openobserve.android.api.feature

import com.openobserve.android.api.context.OpenObserveContext
import com.openobserve.android.core.internal.SdkFeature
import org.mockito.kotlin.any
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import java.util.concurrent.Future

object SdkFeatureMock {
    /**
     * This method is a trick that allows to mock FeatureScope.getContextFuture extension method.
     */
    fun create(future: Future<OpenObserveContext?>? = null): FeatureScope = mock<SdkFeature> {
        on { getContextFuture(any()) } doReturn future
    }
}
