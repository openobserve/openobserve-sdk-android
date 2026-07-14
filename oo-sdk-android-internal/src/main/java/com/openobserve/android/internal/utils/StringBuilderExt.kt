/*
 * Unless explicitly stated otherwise all files in this repository are licensed under the Apache License Version 2.0.
 * This product includes software developed at OpenObserve (https://www.openobserve.ai/).
 * Copyright 2016-Present OpenObserve, Inc.
 */

package com.openobserve.android.internal.utils

/**
 * This utility function helps to replace [joinToString] calls with more efficient [StringBuilder] methods calls.
 * In case when content should be separated with some separator, it's handy to add it in front of a new string only
 * if buffer already contains some data.
 * @param str string that should be added to the buffer only if it already contains some data.
 */
fun StringBuilder.appendIfNotEmpty(str: String) = apply {
    if (isNotEmpty()) append(str)
}

/**
 * This utility function helps to replace [joinToString] calls with more efficient [StringBuilder] methods calls.
 * In case when content should be separated with some separator, it's handy to add it in front of a new string only
 * if buffer already contains some data.
 * @param char char that should be added to the buffer only if it already contains some data.
 */
fun StringBuilder.appendIfNotEmpty(char: Char) = apply {
    if (isNotEmpty()) append(char)
}
