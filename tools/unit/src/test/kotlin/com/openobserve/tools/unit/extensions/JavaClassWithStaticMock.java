/*
 * Unless explicitly stated otherwise all files in this repository are licensed under the Apache License Version 2.0.
 * This product includes software developed at OpenObserve (https://www.openobserve.ai/).
 * Copyright 2016-Present OpenObserve, Inc.
 */

package com.openobserve.tools.unit.extensions;

import org.mockito.Mockito;

public class JavaClassWithStaticMock {
    @SuppressWarnings("unused")
    static Object BAD_MOCK = Mockito.mock(Object.class);
}
