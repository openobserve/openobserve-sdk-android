/*
 * Unless explicitly stated otherwise all files in this repository are licensed under the Apache License Version 2.0.
 * This product includes software developed at OpenObserve (https://www.openobserve.ai/).
 * Copyright 2016-Present OpenObserve, Inc.
 */
package com.openobserve.android.trace.api.span

/**
 * A writer is responsible for sending collected spans.
 * This wrapper is required to not expose [CoreTracer] wrapper to dependent modules
 */
interface OpenObserveSpanWriter
