/*
 * Unless explicitly stated otherwise all files in this repository are licensed under the Apache License Version 2.0.
 * This product includes software developed at OpenObserve (https://www.openobserve.ai/).
 * Copyright 2016-Present OpenObserve, Inc.
 */

package com.openobserve.android.sessionreplay.internal.net

import com.openobserve.android.api.InternalLogger
import com.openobserve.android.api.context.OpenObserveContext
import com.openobserve.android.api.net.Request
import com.openobserve.android.api.net.RequestExecutionContext
import com.openobserve.android.api.net.RequestFactory
import com.openobserve.android.api.storage.RawBatchEvent
import okhttp3.RequestBody
import okio.Buffer
import java.io.EOFException
import java.io.IOException
import java.util.UUID

internal class ResourcesRequestFactory(
    internal val customEndpointUrl: String?,
    private val internalLogger: InternalLogger,
    private val resourceRequestBodyFactory: ResourceRequestBodyFactory =
        ResourceRequestBodyFactory(internalLogger)
) : RequestFactory {

    @Suppress("ThrowingInternalException")
    override fun create(
        context: OpenObserveContext,
        executionContext: RequestExecutionContext,
        batchData: List<RawBatchEvent>,
        batchMetadata: ByteArray?
    ): Request? {
        val requestBody = resourceRequestBodyFactory
            .create(batchData) ?: return null

        return resolveRequest(context, requestBody)
    }

    private fun resolveRequest(context: OpenObserveContext, body: RequestBody): Request? {
        val bodyAsByteArray = convertBodyToByteArray(body) ?: return null
        val requestId = UUID.randomUUID().toString()
        val description = UPLOAD_DESCRIPTION
        val headers = resolveHeaders(context, requestId)
        val requestUrl = buildUrl(context)
        return Request(
            requestId,
            description,
            requestUrl,
            headers,
            body = bodyAsByteArray,
            contentType = body.contentType().toString()
        )
    }

    private fun convertBodyToByteArray(body: RequestBody): ByteArray? {
        var result: ByteArray? = null
        val buffer = Buffer()

        try {
            body.writeTo(buffer)
        } catch (e: IOException) {
            internalLogger.log(
                level = InternalLogger.Level.ERROR,
                target = InternalLogger.Target.MAINTAINER,
                messageBuilder = { ERROR_CONVERTING_BODY_TO_BYTEARRAY },
                throwable = e
            )
        }

        try {
            result = buffer.readByteArray()
        } catch (e: EOFException) {
            internalLogger.log(
                level = InternalLogger.Level.ERROR,
                target = InternalLogger.Target.MAINTAINER,
                messageBuilder = { ERROR_CONVERTING_BODY_TO_BYTEARRAY },
                throwable = e
            )
        }

        return result
    }

    private fun resolveHeaders(ooContext: OpenObserveContext, requestId: String): Map<String, String> {
        return mapOf(
            RequestFactory.HEADER_API_KEY to ooContext.clientToken,
            RequestFactory.HEADER_EVP_ORIGIN to ooContext.source,
            RequestFactory.HEADER_EVP_ORIGIN_VERSION to ooContext.sdkVersion,
            RequestFactory.HEADER_REQUEST_ID to requestId
        )
    }

    private fun buildUrl(ooContext: OpenObserveContext): String {
        return customEndpointUrl ?: (ooContext.site.intakeEndpoint + "/api/v2/replay")
    }

    companion object {
        internal const val APPLICATION_ID = "application_id"
        internal const val UPLOAD_DESCRIPTION = "Session Replay Resource Upload Request"
        internal const val ERROR_CONVERTING_BODY_TO_BYTEARRAY = "Error converting request body to bytearray"
    }
}
