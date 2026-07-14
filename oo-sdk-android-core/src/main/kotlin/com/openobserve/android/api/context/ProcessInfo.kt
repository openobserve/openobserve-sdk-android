/*
 * Unless explicitly stated otherwise all files in this repository are licensed under the Apache License Version 2.0.
 * This product includes software developed at OpenObserve (https://www.openobserve.ai/).
 * Copyright 2016-Present OpenObserve, Inc.
 */

package com.openobserve.android.api.context

/**
 * Holds information about the current process.
 * @property isMainProcess whether this is the main or a secondary process for the app
 */
data class ProcessInfo(val isMainProcess: Boolean)
