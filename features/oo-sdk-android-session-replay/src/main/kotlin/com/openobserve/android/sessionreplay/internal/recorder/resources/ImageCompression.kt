/*
 * Unless explicitly stated otherwise all files in this repository are licensed under the Apache License Version 2.0.
 * This product includes software developed at OpenObserve (https://www.openobserve.ai/).
 * Copyright 2016-Present OpenObserve, Inc.
 */

package com.openobserve.android.sessionreplay.internal.recorder.resources

import android.graphics.Bitmap
import java.io.ByteArrayOutputStream

/**
 * Interface for handling image compression formats.
 */
internal interface ImageCompression {

    /**
     * Compress the bitmap to a [ByteArrayOutputStream].
     */
    fun compressBitmap(bitmap: Bitmap): ByteArray
}
