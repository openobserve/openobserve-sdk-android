/*
 * Unless explicitly stated otherwise all files in this repository are licensed under the Apache License Version 2.0.
 * This product includes software developed at OpenObserve (https://www.openobserve.ai/).
 * Copyright 2016-Present OpenObserve, Inc.
 */

package com.openobserve.android.tests.elmyr

import com.openobserve.android.api.instrumentation.network.ExtendedRequestInfo
import com.openobserve.android.api.instrumentation.network.HttpRequestBody
import com.openobserve.android.api.instrumentation.network.HttpRequestInfo
import com.openobserve.android.api.instrumentation.network.HttpRequestInfoBuilder
import com.openobserve.android.api.instrumentation.network.MutableHttpRequestInfo
import com.openobserve.android.internal.network.HttpSpec
import fr.xgouchet.elmyr.Forge
import fr.xgouchet.elmyr.ForgeryFactory

class RequestInfoForgeryFactory : ForgeryFactory<HttpRequestInfo> {
    override fun getForgery(forge: Forge): HttpRequestInfo {
        return StubRequestInfo(
            url = forge.aStringMatching("https://[a-z0-9]+\\.com"),
            headers = forge.aMap { aString() to aList { aString() } },
            contentType = forge.anAlphabeticalString(),
            method = forge.anElementFrom(HttpSpec.Method.values()),
            contentLength = forge.aNullable { aLong(min = 0) },
            tags = forge.aMap { anAlphabeticalString() to anAlphabeticalString() }
        )
    }

    private data class StubRequestInfo(
        override val url: String,
        override val headers: Map<String, List<String>>,
        override val contentType: String?,
        override val method: String,
        val contentLength: Long?,
        val tags: Map<Any, Any?>
    ) : HttpRequestInfo, ExtendedRequestInfo, MutableHttpRequestInfo {

        @Suppress("UNCHECKED_CAST")
        override fun <T> tag(type: Class<out T>): T? = tags[type] as? T

        override fun contentLength(): Long? = contentLength
        override fun newBuilder(): HttpRequestInfoBuilder = StubHttpRequestInfoBuilder(this.copy())
    }

    private data class StubHttpRequestInfoBuilder(private var request: StubRequestInfo) : HttpRequestInfoBuilder {
        override fun setUrl(url: String) = apply { request = request.copy(url = url) }

        override fun addHeader(key: String, vararg values: String) = apply {
            request = request.copy(headers = request.headers.toMutableMap().also { it[key] = values.asList() })
        }

        override fun removeHeader(key: String) = apply {
            request = request.copy(headers = request.headers.toMutableMap().also { it.remove(key) })
        }

        override fun <T> addTag(type: Class<in T>, tag: T?) = apply {
            request = request.copy(tags = request.tags.toMutableMap().also { it[type] = tag })
        }

        override fun setMethod(
            method: String,
            body: HttpRequestBody?
        ) = apply { request = request.copy(method = method) }

        override fun build(): HttpRequestInfo = request.copy()
    }
}

class MutableRequestInfoForgeryFactory : ForgeryFactory<MutableHttpRequestInfo> {
    override fun getForgery(forge: Forge): MutableHttpRequestInfo {
        return RequestInfoForgeryFactory().getForgery(forge) as MutableHttpRequestInfo
    }
}
