/*
 * Unless explicitly stated otherwise all files in this repository are licensed under the Apache License Version 2.0.
 * This product includes software developed at OpenObserve (https://www.openobserve.ai/).
 * Copyright 2016-Present OpenObserve, Inc.
 */

package com.openobserve.android.internal.data

import com.openobserve.tools.annotation.NoOpImplementation

/**
 * Interface to define the preference storage.
 */
@NoOpImplementation
@Suppress("TooManyFunctions")
interface PreferencesStorage {

    /**
     * Put a float value in the storage.
     */
    fun putFloat(key: String, value: Float)

    /**
     * Get a float value from the storage.
     */
    fun getFloat(key: String, defaultValue: Float = 0f): Float

    /**
     * Put an integer value in the storage.
     */
    fun putInt(key: String, value: Int)

    /**
     * Get an integer value from the storage.
     */
    fun getInt(key: String, defaultValue: Int = 0): Int

    /**
     * Put a string value in the storage.
     */
    fun getString(key: String, defaultValue: String? = null): String?

    /**
     * Put a string value in the storage.
     */
    fun putString(key: String, value: String)

    /**
     * Get a string set value from the storage.
     */
    fun getStringSet(key: String, defaultValue: Set<String> = emptySet()): Set<String>

    /**
     * Put a string set value in the storage.
     */
    fun putStringSet(key: String, value: Set<String>)

    /**
     * Put a boolean value in the storage.
     */
    fun putBoolean(key: String, value: Boolean)

    /**
     * Get a boolean value from the storage.
     */
    fun getBoolean(key: String, defaultValue: Boolean = false): Boolean

    /**
     * Remove a value from the storage.
     */
    fun remove(key: String)

    /**
     * Clear all values from the storage.
     */
    fun clear()
}
