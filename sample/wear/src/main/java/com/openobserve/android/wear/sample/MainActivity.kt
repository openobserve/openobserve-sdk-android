/*
 * Unless explicitly stated otherwise all files in this repository are licensed under the Apache License Version 2.0.
 * This product includes software developed at OpenObserve (https://www.openobserve.ai/).
 * Copyright 2016-Present OpenObserve, Inc.
 */

package com.openobserve.android.wear.sample

import android.app.Activity
import android.os.Bundle
import com.openobserve.android.wear.sample.databinding.ActivityMainBinding
import io.opentelemetry.api.GlobalOpenTelemetry

@Suppress("UndocumentedPublicProperty", "UndocumentedPublicClass")
class MainActivity : Activity() {

    private lateinit var binding: ActivityMainBinding
    private val tracer = GlobalOpenTelemetry.get().getTracer("MainActivity")

    override fun onCreate(savedInstanceState: Bundle?) {
        val onCreateSpan = tracer.spanBuilder("onCreate").startSpan()
        onCreateSpan.setAttribute("activity", MainActivity::class.java.simpleName)
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        onCreateSpan.end()
    }
}
