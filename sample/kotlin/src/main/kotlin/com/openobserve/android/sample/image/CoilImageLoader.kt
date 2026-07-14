/*
 * Unless explicitly stated otherwise all files in this repository are licensed under the Apache License Version 2.0.
 * This product includes software developed at OpenObserve (https://www.openobserve.ai/).
 * Copyright 2016-Present OpenObserve, Inc.
 */

package com.openobserve.android.sample.image

import android.content.Context
import android.widget.ImageView
import coil.Coil
import coil.load
import com.openobserve.android.sample.R
import okhttp3.OkHttpClient

internal class CoilImageLoader : ImageLoader {

    override val type: ImageLoaderType = ImageLoaderType.COIL

    override fun load(url: String, imageView: ImageView) {
        imageView.load(url) {
            placeholder(R.drawable.ph_default)
            error(R.drawable.ph_error)
        }
    }

    companion object {
        fun initialize(context: Context, okHttpClient: OkHttpClient) {
            val imageLoader = coil.ImageLoader.Builder(context).okHttpClient(okHttpClient).build()
            Coil.setImageLoader(imageLoader)
        }
    }
}
