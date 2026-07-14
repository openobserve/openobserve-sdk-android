/*
 * Unless explicitly stated otherwise all files in this repository are licensed under the Apache License Version 2.0.
 * This product includes software developed at OpenObserve (https://www.openobserve.ai/).
 * Copyright 2016-Present OpenObserve, Inc.
 */

package com.openobserve.android.sessionreplay.internal.net

import com.openobserve.android.OpenObserveSite
import com.openobserve.android.api.InternalLogger
import com.openobserve.android.api.context.OpenObserveContext
import com.openobserve.android.api.feature.Feature
import com.openobserve.android.api.net.Request
import com.openobserve.android.api.net.RequestExecutionContext
import com.openobserve.android.api.net.RequestFactory
import com.openobserve.android.api.net.RequestFactory.Companion.HEADER_API_KEY
import com.openobserve.android.api.net.RequestFactory.Companion.HEADER_EVP_ORIGIN
import com.openobserve.android.api.net.RequestFactory.Companion.HEADER_EVP_ORIGIN_VERSION
import com.openobserve.android.api.storage.RawBatchEvent
import com.openobserve.android.sessionreplay.forge.ForgeConfigurator
import com.openobserve.android.sessionreplay.internal.net.ResourcesRequestFactory.Companion.APPLICATION_ID
import com.openobserve.android.sessionreplay.internal.net.ResourcesRequestFactory.Companion.UPLOAD_DESCRIPTION
import fr.xgouchet.elmyr.Forge
import fr.xgouchet.elmyr.annotation.Forgery
import fr.xgouchet.elmyr.annotation.StringForgery
import fr.xgouchet.elmyr.junit5.ForgeConfiguration
import fr.xgouchet.elmyr.junit5.ForgeExtension
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okio.Buffer
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.junit.jupiter.api.extension.Extensions
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.junit.jupiter.MockitoSettings
import org.mockito.kotlin.whenever
import org.mockito.quality.Strictness

@Extensions(
    ExtendWith(MockitoExtension::class),
    ExtendWith(ForgeExtension::class)
)
@MockitoSettings(strictness = Strictness.LENIENT)
@ForgeConfiguration(ForgeConfigurator::class)
internal class ResourcesRequestFactoryTest {
    private lateinit var testedRequestFactory: ResourcesRequestFactory

    @Mock
    lateinit var mockResourceRequestBodyFactory: ResourceRequestBodyFactory

    private lateinit var fakeRawBatchEvents: List<RawBatchEvent>

    @Mock
    lateinit var mockRequestBody: RequestBody

    @Mock
    lateinit var mockInternalLogger: InternalLogger

    private lateinit var fakeMediaType: MediaType

    @Mock
    lateinit var mockOpenObserveSite: OpenObserveSite

    @StringForgery
    lateinit var fakeApplicationId: String

    @Mock
    lateinit var fakeOpenObserveContext: OpenObserveContext

    @Forgery
    lateinit var fakeExecutionContext: RequestExecutionContext

    @BeforeEach
    fun `set up`(forge: Forge) {
        val fakeRumFeature = mapOf(APPLICATION_ID to fakeApplicationId)
        whenever(mockOpenObserveSite.intakeEndpoint).thenReturn(OpenObserveSite.US1.toString())
        whenever(fakeOpenObserveContext.site).thenReturn(mockOpenObserveSite)
        val fakeFeaturesContext = mapOf(Feature.RUM_FEATURE_NAME to fakeRumFeature)
        whenever(fakeOpenObserveContext.featuresContext).thenReturn(fakeFeaturesContext)

        fakeRawBatchEvents = forge.aList { forge.getForgery() }
        whenever(mockResourceRequestBodyFactory.create(fakeRawBatchEvents))
            .thenReturn(mockRequestBody)

        fakeMediaType = forge.anElementFrom(
            listOf(
                MultipartBody.FORM,
                MultipartBody.ALTERNATIVE,
                MultipartBody.MIXED,
                MultipartBody.PARALLEL
            )
        )
        whenever(mockRequestBody.contentType()).thenReturn(fakeMediaType)

        testedRequestFactory = ResourcesRequestFactory(
            customEndpointUrl = null,
            internalLogger = mockInternalLogger,
            resourceRequestBodyFactory = mockResourceRequestBodyFactory
        )
    }

    @Test
    fun `M return valid request W create()`() {
        // When
        val request = testedRequestFactory.create(
            fakeOpenObserveContext,
            fakeExecutionContext,
            fakeRawBatchEvents,
            null
        )

        // Then
        assertThat(request).isInstanceOf(Request::class.java)
        requireNotNull(request)
        val requestHeaders = request.headers
        val requestBody = request.body
        assertThat(requestHeaders[HEADER_API_KEY])
            .isEqualTo(fakeOpenObserveContext.clientToken)
        assertThat(requestHeaders[HEADER_EVP_ORIGIN])
            .isEqualTo(fakeOpenObserveContext.source)
        assertThat(requestHeaders[HEADER_EVP_ORIGIN_VERSION])
            .isEqualTo(fakeOpenObserveContext.sdkVersion)
        assertThat(requestBody).isEqualTo(mockRequestBody.toByteArray())

        assertThat(request.description).isEqualTo(UPLOAD_DESCRIPTION)
        assertThat(request.url).isEqualTo("${fakeOpenObserveContext.site.intakeEndpoint}/api/v2/replay")
    }

    @Test
    fun `M  return valid request W create() { custom endpoint }`(
        @StringForgery(regex = "https://[a-z]+\\.com(/[a-z]+)+") fakeEndpoint: String
    ) {
        // Given
        testedRequestFactory = ResourcesRequestFactory(
            customEndpointUrl = fakeEndpoint,
            internalLogger = mockInternalLogger,
            resourceRequestBodyFactory = mockResourceRequestBodyFactory
        )

        // When
        val request = testedRequestFactory.create(
            fakeOpenObserveContext,
            fakeExecutionContext,
            fakeRawBatchEvents,
            null
        )

        // Then
        requireNotNull(request)
        assertThat(request.url).isEqualTo(fakeEndpoint)
        assertThat(request.id).isEqualTo(request.headers[RequestFactory.HEADER_REQUEST_ID])
        assertThat(request.description).isEqualTo(UPLOAD_DESCRIPTION)
        assertThat(request.body).isEqualTo(mockRequestBody.toByteArray())
    }

    private fun RequestBody.toByteArray(): ByteArray {
        val buffer = Buffer()
        writeTo(buffer)
        return buffer.readByteArray()
    }
}
