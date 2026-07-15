/*
 * Unless explicitly stated otherwise all files in this repository are licensed under the Apache License Version 2.0.
 * This product includes software developed at Datadog (https://www.datadoghq.com/).
 * Copyright 2016-Present Datadog, Inc.
 */
package com.openobserve.android.trace.internal

import com.openobserve.android.trace.api.span.OpenObserveSpanWriter
import com.openobserve.trace.common.writer.Writer

internal class OpenObserveSpanWriterWrapper(internal val delegate: Writer) : OpenObserveSpanWriter
