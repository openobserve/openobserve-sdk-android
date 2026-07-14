/*
 * Unless explicitly stated otherwise all files in this repository are licensed under the Apache License Version 2.0.
 * This product includes software developed at OpenObserve (https://www.openobserve.ai/).
 * Copyright 2016-Present OpenObserve, Inc.
 */
package com.openobserve.android.utils.forge

import fr.xgouchet.elmyr.Forge
import fr.xgouchet.elmyr.ForgeryFactory
import org.chromium.net.Proxy
import org.mockito.kotlin.any
import org.mockito.kotlin.doAnswer
import org.mockito.kotlin.mock
import java.util.concurrent.Executor

internal class ProxyForgeFactory : ForgeryFactory<Proxy> {
    override fun getForgery(forge: Forge) = Proxy(
        forge.anElementFrom(Proxy.HTTP, Proxy.HTTPS),
        forge.aString(),
        forge.anInt(min = 1, max = 65535),
        mock<Executor>() {
            on { execute(any()) } doAnswer {
                it.getArgument<() -> Unit>(0).invoke()
            }
        },
        object : Proxy.Callback() {

            @Deprecated("Deprecated in Java")
            override fun onBeforeTunnelRequest() = emptyList<Map.Entry<String, String>>()

            override fun onTunnelHeadersReceived(
                responseHeaders: List<Map.Entry<String, String>>,
                statusCode: Int
            ) = false
        }
    )
}
