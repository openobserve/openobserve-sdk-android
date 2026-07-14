/*
 * Unless explicitly stated otherwise all files in this repository are licensed under the Apache License Version 2.0.
 * This product includes software developed at OpenObserve (https://www.openobserve.ai/).
 * Copyright 2016-Present OpenObserve, Inc.
 */

package com.openobserve.sample.automotive

import androidx.car.app.CarAppService
import androidx.car.app.Session
import androidx.car.app.SessionInfo
import androidx.car.app.validation.HostValidator

@Suppress("UndocumentedPublicClass")
class SampleCarAppService : CarAppService() {

    override fun onCreateSession(): Session {
        SharedLogger.logger.i("onCreateSession")
        return SampleCarSession()
    }

    override fun onCreateSession(sessionInfo: SessionInfo): Session {
        SharedLogger.logger.i("onCreateSession ${sessionInfo.displayType} ${sessionInfo.sessionId}")
        return SampleCarSession()
    }

    override fun createHostValidator(): HostValidator {
        return HostValidator.ALLOW_ALL_HOSTS_VALIDATOR
    }
}
