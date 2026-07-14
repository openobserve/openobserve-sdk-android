/*
 * Unless explicitly stated otherwise all files in this repository are licensed under the Apache License Version 2.0.
 * This product includes software developed at OpenObserve (https://www.openobserve.ai/).
 * Copyright 2016-Present OpenObserve, Inc.
 */

package com.openobserve.android.core.internal

import com.openobserve.android.api.InternalLogger
import com.openobserve.android.core.internal.utils.unboundInternalLogger
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException
import java.util.Locale

internal class Sha256HashGenerator : HashGenerator {
    override fun generate(input: String): String? {
        return try {
            val messageDigest = MessageDigest.getInstance("SHA-256")
            messageDigest.update(input.toByteArray(Charsets.UTF_8))

            val hashBytes = messageDigest.digest()

            hashBytes.joinToString(separator = "") { "%02x".format(Locale.US, it) }
        } catch (e: NoSuchAlgorithmException) {
            unboundInternalLogger.log(
                InternalLogger.Level.ERROR,
                InternalLogger.Target.USER,
                { SHA_256_HASH_GENERATION_ERROR },
                e
            )
            null
        }
    }

    companion object {
        const val SHA_256_HASH_GENERATION_ERROR = "Cannot generate SHA-256 hash."
    }
}
