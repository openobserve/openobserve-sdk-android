/*
 * Unless explicitly stated otherwise all files in this repository are licensed under the Apache License Version 2.0.
 * This product includes software developed at OpenObserve (https://www.openobserve.ai/).
 * Copyright 2016-Present OpenObserve, Inc.
 */

package com.openobserve.android.api.context

/**
 * Holds information about the current account.
 * @property id a unique identifier for the account, or null.
 * @property name the name of the account, or null.
 * @property extraInfo a dictionary of extra information to the current account.
 */
data class AccountInfo(
    val id: String,
    val name: String? = null,
    val extraInfo: Map<String, Any?> = emptyMap()
)
