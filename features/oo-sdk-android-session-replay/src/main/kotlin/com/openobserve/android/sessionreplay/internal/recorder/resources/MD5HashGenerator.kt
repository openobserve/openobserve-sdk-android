/*
 * Unless explicitly stated otherwise all files in this repository are licensed under the Apache License Version 2.0.
 * This product includes software developed at OpenObserve (https://www.openobserve.ai/).
 * Copyright 2016-Present OpenObserve, Inc.
 */

package com.openobserve.android.sessionreplay.internal.recorder.resources

import com.openobserve.android.api.InternalLogger
import com.openobserve.android.internal.utils.toHexString
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException

internal class MD5HashGenerator(
    private val logger: InternalLogger
) : HashGenerator {
    override fun generate(input: ByteArray): String? {
        return try {
            val messageDigest = MessageDigest.getInstance("MD5")
            messageDigest.update(input)

            val hashBytes = messageDigest.digest()

            hashBytes.toHexString()
        } catch (e: NoSuchAlgorithmException) {
            logger.log(
                InternalLogger.Level.ERROR,
                listOf(InternalLogger.Target.MAINTAINER, InternalLogger.Target.TELEMETRY),
                { MD5_HASH_GENERATION_ERROR },
                e
            )
            null
        }
    }

    private companion object {
        private const val MD5_HASH_GENERATION_ERROR = "Cannot generate MD5 hash."
    }
}
