/*
 * Unless explicitly stated otherwise all files in this repository are licensed under the Apache License Version 2.0.
 * This product includes software developed at OpenObserve (https://www.openobserve.ai/).
 * Copyright 2016-Present OpenObserve, Inc.
 */
package com.openobserve.android.cronet.internal

import org.chromium.net.UrlRequest
import java.nio.ByteBuffer

internal abstract class CronetUrlRequestWrapper : UrlRequest() {

    abstract val delegate: UrlRequest?

    override fun start() {
        delegate?.start()
    }

    override fun followRedirect() {
        delegate?.followRedirect()
    }

    override fun read(buffer: ByteBuffer) {
        delegate?.read(buffer)
    }

    override fun cancel() {
        delegate?.cancel()
    }

    override fun isDone(): Boolean {
        return delegate?.isDone ?: false
    }

    override fun getStatus(listener: StatusListener) {
        delegate?.getStatus(listener)
    }
}
