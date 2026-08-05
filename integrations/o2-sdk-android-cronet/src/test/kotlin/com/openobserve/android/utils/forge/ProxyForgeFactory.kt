/*
 * Unless explicitly stated otherwise all files in this repository are licensed under the Apache License Version 2.0.
 * This product includes software developed at Datadog (https://www.datadoghq.com/).
 * Copyright 2016-Present Datadog, Inc.
 */
package com.openobserve.android.utils.forge

import fr.xgouchet.elmyr.Forge
import fr.xgouchet.elmyr.ForgeryFactory
import org.chromium.net.Proxy

internal class ProxyForgeFactory : ForgeryFactory<Proxy> {
    // Proxy(scheme, host, port, callback) -- four arguments in the Cronet version this
    // module ships against. The factory previously passed an Executor as a fifth, which
    // only ever compiled while the test dependency floated on `:+`; it matched neither
    // the pinned 141.7340.3 nor the 500.0.1 that float had drifted to.
    override fun getForgery(forge: Forge) = Proxy(
        forge.anElementFrom(Proxy.HTTP, Proxy.HTTPS),
        forge.aString(),
        forge.anInt(min = 1, max = 65535),
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
