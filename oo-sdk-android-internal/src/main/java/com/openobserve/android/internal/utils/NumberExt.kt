/*
 * Unless explicitly stated otherwise all files in this repository are licensed under the Apache License Version 2.0.
 * This product includes software developed at OpenObserve (https://www.openobserve.ai/).
 * Copyright 2016-Present OpenObserve, Inc.
 */

package com.openobserve.android.internal.utils

import java.math.BigInteger

/**
 * Radix used to convert numbers to hexadecimal strings.
 */
private const val HEX_RADIX: Int = 16

/**
 * Converts this [Int] into hexadecimal representation.
 */
fun Int.toHexString(): String = toString(HEX_RADIX)

/**
 * Converts this [Long] into hexadecimal representation.
 */
fun Long.toHexString(): String = toString(HEX_RADIX)

/**
 * Converts this [BigInteger] into hexadecimal representation.
 */
fun BigInteger.toHexString(): String {
    return toLong().toHexString()
}
