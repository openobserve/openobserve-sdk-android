/*
 * Unless explicitly stated otherwise all files in this repository are licensed under the Apache License Version 2.0.
 * This product includes software developed at OpenObserve (https://www.openobserve.ai/).
 * Copyright 2016-Present OpenObserve, Inc.
 */

package com.openobserve.android.sessionreplay

/**
 * Defines the Session Replay privacy policy when recording touch interactions.
 * @see TouchPrivacy.SHOW
 * @see TouchPrivacy.HIDE
 */
enum class TouchPrivacy : PrivacyLevel {
    /**
     * All touch interactions will be recorded.
     */
    SHOW,

    /**
     * No touch interactions will be recorded.
     */
    HIDE
}
