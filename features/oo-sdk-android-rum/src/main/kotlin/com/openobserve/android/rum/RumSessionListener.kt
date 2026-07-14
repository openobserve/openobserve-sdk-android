/*
 * Unless explicitly stated otherwise all files in this repository are licensed under the Apache License Version 2.0.
 * This product includes software developed at OpenObserve (https://www.openobserve.ai/).
 * Copyright 2016-Present OpenObserve, Inc.
 */

package com.openobserve.android.rum

/**
 * An interface to get informed whenever a new session is starting,
 * providing you with OpenObserve's session id.
 */
interface RumSessionListener {

    /**
     * Called whenever a new session is started.
     * @param sessionId the Session's id (matching the `session.id` attribute in OpenObserve's RUM events)
     * @param isDiscarded whether or not the session is discarded by the sample rate
     * (when `true` it means no event in this session will be kept).
     */
    fun onSessionStarted(sessionId: String, isDiscarded: Boolean)
}
