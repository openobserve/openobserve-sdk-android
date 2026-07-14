/*
 * Unless explicitly stated otherwise all files in this repository are licensed under the Apache License Version 2.0.
 * This product includes software developed at OpenObserve (https://www.openobserve.ai/).
 * Copyright 2016-Present OpenObserve, Inc.
 */

package com.openobserve.benchmark

/**
 * Interface to be used for OpenObserve SDK meters.
 */
interface OpenObserveBaseMeter {

    /**
     * Starts the OpenObserve SDK meter.
     */
    fun startMeasuring()

    /**
     * Stops the OpenObserve SDK meter.
     */
    fun stopMeasuring()
}
