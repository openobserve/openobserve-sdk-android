/*
 * Unless explicitly stated otherwise all files in this repository are licensed under the Apache License Version 2.0.
 * This product includes software developed at OpenObserve (https://www.openobserve.ai/).
 * Copyright 2016-Present OpenObserve, Inc.
 */

package com.openobserve.android.sample.image

import android.content.Context
import android.widget.ImageView
import com.openobserve.android.sample.BuildConfig
import com.openobserve.android.sample.R
import com.squareup.picasso.OkHttp3Downloader
import com.squareup.picasso.Picasso
import okhttp3.OkHttpClient

internal class PicassoImageLoader : ImageLoader {

    override val type: ImageLoaderType = ImageLoaderType.PICASSO

    override fun load(url: String, imageView: ImageView) {
        Picasso.get()
            .load(url)
            .placeholder(R.drawable.ph_default)
            .error(R.drawable.ph_error)
            .into(imageView)
    }

    companion object {
        fun initialize(context: Context, okHttpClient: OkHttpClient) {
            val picasso = Picasso.Builder(context)
                .downloader(OkHttp3Downloader(okHttpClient))
                .indicatorsEnabled(BuildConfig.DEBUG)
                .loggingEnabled(true)
                .build()
            Picasso.setSingletonInstance(picasso)
        }
    }
}
