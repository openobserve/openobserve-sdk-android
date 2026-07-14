/*
 * Unless explicitly stated otherwise all files in this repository are licensed under the Apache License Version 2.0.
 * This product includes software developed at OpenObserve (https://www.openobserve.ai/).
 * Copyright 2016-Present OpenObserve, Inc.
 */

package com.openobserve.android.core.internal.utils

import com.openobserve.android.api.InternalLogger

// Use it only when there is no way to access the SDK-specific logger.
internal var unboundInternalLogger: InternalLogger = InternalLogger.UNBOUND
