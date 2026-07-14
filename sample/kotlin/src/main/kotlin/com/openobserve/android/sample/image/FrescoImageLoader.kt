/*
 * Unless explicitly stated otherwise all files in this repository are licensed under the Apache License Version 2.0.
 * This product includes software developed at OpenObserve (https://www.openobserve.ai/).
 * Copyright 2016-Present OpenObserve, Inc.
 */

package com.openobserve.android.sample.image

import android.content.Context
import android.widget.ImageView
import androidx.core.net.toUri
import com.openobserve.android.OpenObserve
import com.openobserve.android.fresco.OpenObserveFrescoCacheListener
import com.openobserve.android.rum.GlobalRumMonitor
import com.openobserve.android.rum.RumErrorSource
import com.openobserve.android.sample.BuildConfig
import com.facebook.cache.disk.DiskCacheConfig
import com.facebook.common.util.ByteConstants
import com.facebook.drawee.backends.pipeline.DraweeConfig
import com.facebook.drawee.backends.pipeline.Fresco
import com.facebook.drawee.view.SimpleDraweeView
import com.facebook.imagepipeline.backends.okhttp3.OkHttpImagePipelineConfigFactory
import com.facebook.imagepipeline.cache.MemoryCacheParams
import okhttp3.OkHttpClient

internal class FrescoImageLoader : ImageLoader {

    override val type: ImageLoaderType = ImageLoaderType.FRESCO

    override fun load(url: String, imageView: ImageView) {
        if (imageView is SimpleDraweeView) {
            imageView.setImageURI(url.toUri())
        } else {
            GlobalRumMonitor.get(OpenObserve.getInstance()).addError(
                "Unable to load Fresco image in non Drawee View",
                RumErrorSource.SOURCE,
                null
            )
        }
    }

    companion object {

        private val MAX_HEAP_SIZE = Runtime.getRuntime().maxMemory().toInt()
        private val MAX_MEMORY_CACHE_SIZE = MAX_HEAP_SIZE / 4
        private const val MAX_DISK_CACHE_SIZE = 40L * ByteConstants.MB

        fun initialize(context: Context, okHttpClient: OkHttpClient) {
            val diskConfigBuilder = DiskCacheConfig.newBuilder(context)
                .setCacheEventListener(OpenObserveFrescoCacheListener(OpenObserve.getInstance()))
                .setMaxCacheSize(MAX_DISK_CACHE_SIZE)
            val config = OkHttpImagePipelineConfigFactory
                .newBuilder(context, okHttpClient)
                .setBitmapMemoryCacheParamsSupplier {
                    MemoryCacheParams(
                        MAX_MEMORY_CACHE_SIZE,
                        Int.MAX_VALUE,
                        MAX_MEMORY_CACHE_SIZE,
                        Int.MAX_VALUE,
                        Int.MAX_VALUE
                    )
                }
                .setMainDiskCacheConfig(diskConfigBuilder.build())
                .build()
            val draweeConfig = DraweeConfig.newBuilder()
                .setDrawDebugOverlay(BuildConfig.DEBUG)
                .build()
            Fresco.initialize(context, config, draweeConfig)
        }
    }
}
