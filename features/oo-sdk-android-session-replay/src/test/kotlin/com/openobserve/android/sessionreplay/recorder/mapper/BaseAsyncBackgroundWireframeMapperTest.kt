/*
 * Unless explicitly stated otherwise all files in this repository are licensed under the Apache License Version 2.0.
 * This product includes software developed at OpenObserve (https://www.openobserve.ai/).
 * Copyright 2016-Present OpenObserve, Inc.
 */

package com.openobserve.android.sessionreplay.recorder.mapper

import android.view.View

internal abstract class BaseAsyncBackgroundWireframeMapperTest<V : View, WM : WireframeMapper<V>> :
    BaseWireframeMapperTest<V, WM>()
