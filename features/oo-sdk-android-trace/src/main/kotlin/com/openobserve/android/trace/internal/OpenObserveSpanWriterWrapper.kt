/*
 * Unless explicitly stated otherwise all files in this repository are licensed under the Apache License Version 2.0.
 * This product includes software developed at OpenObserve (https://www.openobserve.ai/).
 * Copyright 2016-Present OpenObserve, Inc.
 */
package com.openobserve.android.trace.internal

import com.openobserve.android.trace.api.span.OpenObserveSpanWriter
import com.openobserve.trace.common.writer.Writer

internal class OpenObserveSpanWriterWrapper(internal val delegate: Writer) : OpenObserveSpanWriter
