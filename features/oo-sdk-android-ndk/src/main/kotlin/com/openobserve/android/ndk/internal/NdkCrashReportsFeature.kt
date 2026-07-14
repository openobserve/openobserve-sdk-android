/*
 * Unless explicitly stated otherwise all files in this repository are licensed under the Apache License Version 2.0.
 * This product includes software developed at OpenObserve (https://www.openobserve.ai/).
 * Copyright 2016-Present OpenObserve, Inc.
 */

package com.openobserve.android.ndk.internal

import android.content.Context
import com.openobserve.android.api.InternalLogger
import com.openobserve.android.api.feature.Feature
import com.openobserve.android.api.feature.FeatureSdkCore
import com.openobserve.android.core.InternalSdkCore
import com.openobserve.android.internal.utils.allowThreadDiskReads
import com.openobserve.android.internal.utils.allowThreadDiskWrites
import com.openobserve.android.privacy.TrackingConsent
import com.openobserve.android.privacy.TrackingConsentProviderCallback
import java.io.File
import java.lang.NullPointerException
import java.util.concurrent.TimeUnit

/**
 * An implementation of the [Feature] which will allow to intercept and report the
 * NDK crashes to our logs dashboard.
 */
internal class NdkCrashReportsFeature(
    private val sdkCore: FeatureSdkCore
) : Feature, TrackingConsentProviderCallback {
    private var nativeLibraryLoaded = false

    override val name: String = Feature.NDK_CRASH_REPORTS_FEATURE_NAME

    // region Feature
    @Suppress("ReturnCount")
    override fun onInitialize(appContext: Context) {
        loadNativeLibrary(sdkCore.internalLogger)
        if (!nativeLibraryLoaded) {
            return
        }
        val internalSdkCore = sdkCore as InternalSdkCore
        val rootStorageDir = internalSdkCore.rootStorageDir
        if (rootStorageDir == null) {
            sdkCore.internalLogger.log(
                InternalLogger.Level.WARN,
                InternalLogger.Target.USER,
                { NO_SDK_ROOT_DIR_MESSAGE }
            )
            return
        }
        val ndkCrashesDirs = File(
            rootStorageDir,
            NDK_CRASH_REPORTS_FOLDER
        )
        try {
            allowThreadDiskWrites {
                ndkCrashesDirs.mkdirs()
            }
        } catch (e: SecurityException) {
            sdkCore.internalLogger.log(
                InternalLogger.Level.ERROR,
                InternalLogger.Target.USER,
                { "Unable to create NDK Crash Report folder $ndkCrashesDirs" },
                e
            )
            return
        }
        val nowMs = sdkCore.timeProvider.getDeviceTimestampMillis()
        val nowElapsedNs = sdkCore.timeProvider.getDeviceElapsedTimeNanos()
        val appStartTimestamp =
            TimeUnit.MILLISECONDS.toNanos(nowMs) - nowElapsedNs + sdkCore.appStartTimeNs
        registerSignalHandler(
            ndkCrashesDirs.absolutePath,
            consentToInt(internalSdkCore.trackingConsent),
            TimeUnit.NANOSECONDS.toMillis(appStartTimestamp)
        )
    }

    override fun onStop() {
        if (!nativeLibraryLoaded) {
            return
        }
        unregisterSignalHandler()
    }

    // endregion

    // region TrackingConsentProviderCallback

    override fun onConsentUpdated(previousConsent: TrackingConsent, newConsent: TrackingConsent) {
        if (!nativeLibraryLoaded) {
            return
        }
        updateTrackingConsent(consentToInt(newConsent))
    }

    internal fun consentToInt(newConsent: TrackingConsent): Int {
        return when (newConsent) {
            TrackingConsent.PENDING -> TRACKING_CONSENT_PENDING
            TrackingConsent.GRANTED -> TRACKING_CONSENT_GRANTED
            else -> TRACKING_CONSENT_NOT_GRANTED
        }
    }

    // endregion

    // region NDK

    private fun loadNativeLibrary(internalLogger: InternalLogger) {
        var exception: Throwable? = null
        try {
            allowThreadDiskReads {
                System.loadLibrary("openobserve-ndk")
            }
            nativeLibraryLoaded = true
        } catch (e: SecurityException) {
            exception = e
        } catch (@SuppressWarnings("TooGenericExceptionCaught") e: NullPointerException) {
            exception = e
        } catch (e: UnsatisfiedLinkError) {
            exception = e
        }
        exception?.let {
            internalLogger.log(
                InternalLogger.Level.ERROR,
                InternalLogger.Target.USER,
                { ERROR_LOADING_NATIVE_MESSAGE },
                exception
            )
        }
    }

    private external fun registerSignalHandler(
        storagePath: String,
        consent: Int,
        appStartTimeMs: Long
    )

    private external fun unregisterSignalHandler()

    private external fun updateTrackingConsent(consent: Int)

    // endregion

    internal companion object {
        internal const val NDK_CRASH_REPORTS_FOLDER = "ndk_crash_reports_v2"
        private const val ERROR_LOADING_NATIVE_MESSAGE =
            "We could not load the native library for NDK crash reporting."
        internal const val NO_SDK_ROOT_DIR_MESSAGE =
            "Cannot get a directory for SDK data storage. Please make sure that SDK is initialized."
        internal const val TRACKING_CONSENT_PENDING = 0
        internal const val TRACKING_CONSENT_GRANTED = 1
        internal const val TRACKING_CONSENT_NOT_GRANTED = 2
    }
}
