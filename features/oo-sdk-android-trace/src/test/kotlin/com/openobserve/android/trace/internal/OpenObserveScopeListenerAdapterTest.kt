/*
 * Unless explicitly stated otherwise all files in this repository are licensed under the Apache License Version 2.0.
 * This product includes software developed at OpenObserve (https://www.openobserve.ai/).
 * Copyright 2016-Present OpenObserve, Inc.
 */
package com.openobserve.android.trace.internal

import com.openobserve.android.trace.api.scope.OpenObserveScopeListener
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.junit.jupiter.api.extension.Extensions
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.verify

@Extensions(
    ExtendWith(MockitoExtension::class)
)
class OpenObserveScopeListenerAdapterTest {

    @Mock
    lateinit var delegate: OpenObserveScopeListener

    @Test
    fun `M delegate afterScopeClosed W afterScopeClosed is called`() {
        // Given
        val adapter = OpenObserveScopeListenerAdapter(delegate)

        // When
        adapter.afterScopeClosed()

        // Then
        verify(delegate).afterScopeClosed()
    }

    @Test
    fun `M delegate afterScopeActivated W afterScopeActivated is called`() {
        // Then
        val adapter = OpenObserveScopeListenerAdapter(delegate)

        // When
        adapter.afterScopeActivated()

        // Then
        verify(delegate).afterScopeActivated()
    }
}
