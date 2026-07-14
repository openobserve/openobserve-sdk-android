/*
 * Unless explicitly stated otherwise all files in this repository are licensed under the Apache License Version 2.0.
 * This product includes software developed at OpenObserve (https://www.openobserve.ai/).
 * Copyright 2016-Present OpenObserve, Inc.
 */

package com.openobserve.trace.logger;

import androidx.annotation.NonNull;

import com.openobserve.android.api.InternalLogger;

public final class LoggerFactory {

    @NonNull
    public static Logger getLogger(String name) {
        return new NoOpLogger();
    }
    @NonNull
    public static Logger getLogger(String name, InternalLogger internalLogger) {
        return new OpenObserveCoreTracerLogger(name, internalLogger);
    }

    @NonNull
    public static Logger getLogger(Class<?> clazz) {
        return new NoOpLogger();
    }

    public static ILoggerFactory getILoggerFactory() {
        return new ILoggerFactory() {
            @Override
            public Logger getLogger(String name) {
                return new NoOpLogger();
            }

            @Override
            public Logger getLogger(String name, InternalLogger internalLogger) {
                return new OpenObserveCoreTracerLogger(name, internalLogger);
            }
        };
    }
}
