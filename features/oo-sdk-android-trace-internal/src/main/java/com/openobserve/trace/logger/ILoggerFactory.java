/*
 * Unless explicitly stated otherwise all files in this repository are licensed under the Apache License Version 2.0.
 * This product includes software developed at OpenObserve (https://www.openobserve.ai/).
 * Copyright 2016-Present OpenObserve, Inc.
 */

package com.openobserve.trace.logger;

import androidx.annotation.NonNull;

import com.openobserve.android.api.InternalLogger;

public interface ILoggerFactory {

    @NonNull
    Logger getLogger(String name);

    @NonNull
    Logger getLogger(String name, InternalLogger internalLogger);

}
