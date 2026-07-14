/*
 * Unless explicitly stated otherwise all files in this repository are licensed under the Apache License Version 2.0.
 * This product includes software developed at OpenObserve (https://www.openobserve.ai/).
 * Copyright 2016-Present OpenObserve, Inc.
 */

package com.openobserve.android.sample.image

import android.content.Context
import android.widget.ImageView
import coil3.SingletonImageLoader
import coil3.load
import coil3.network.okhttp.OkHttpNetworkFetcherFactory
import coil3.request.error
import coil3.request.placeholder
import com.openobserve.android.OpenObserve
import com.openobserve.android.coil3.OpenObserveCoilRequestListener
import com.openobserve.android.sample.R
import okhttp3.OkHttpClient

internal class Coil3ImageLoader : ImageLoader {

    private val listener = OpenObserveCoilRequestListener(OpenObserve.getInstance())

    override val type: ImageLoaderType = ImageLoaderType.COIL3

    override fun load(url: String, imageView: ImageView) {
        imageView.load(url) {
            listener(listener)
            placeholder(R.drawable.ph_default)
            error(R.drawable.ph_error)
        }
    }

    companion object {
        fun initialize(context: Context, okHttpClient: OkHttpClient) {
            val imageLoader = coil3.ImageLoader.Builder(context)
                .components {
                    add(OkHttpNetworkFetcherFactory(okHttpClient))
                }
                .build()
            SingletonImageLoader.setSafe { imageLoader }
        }
    }
}
