/*
 * Unless explicitly stated otherwise all files in this repository are licensed under the Apache License Version 2.0.
 * This product includes software developed at OpenObserve (https://www.openobserve.ai/).
 * Copyright 2016-Present OpenObserve, Inc.
 */

package com.openobserve.android.sdk.integration.security

import android.util.Log
import androidx.test.filters.MediumTest
import androidx.test.platform.app.InstrumentationRegistry
import com.openobserve.android.OpenObserve
import com.openobserve.android.core.configuration.Configuration
import com.openobserve.android.log.Logger
import com.openobserve.android.log.Logs
import com.openobserve.android.log.LogsConfiguration
import com.openobserve.android.privacy.TrackingConsent
import com.openobserve.android.rum.GlobalRumMonitor
import com.openobserve.android.rum.Rum
import com.openobserve.android.rum.RumActionType
import com.openobserve.android.rum.RumConfiguration
import com.openobserve.android.rum.RumMonitor
import com.openobserve.android.rum.RumResourceKind
import com.openobserve.android.rum.RumResourceMethod
import com.openobserve.android.security.Encryption
import com.openobserve.android.sessionreplay.SessionReplay
import com.openobserve.android.sessionreplay.SessionReplayConfiguration
import com.openobserve.android.trace.OpenObserveTracing
import com.openobserve.android.trace.GlobalOpenObserveTracer
import com.openobserve.android.trace.Trace
import com.openobserve.android.trace.TraceConfiguration
import com.openobserve.android.trace.api.tracer.OpenObserveTracer
import fr.xgouchet.elmyr.junit4.ForgeRule
import org.assertj.core.api.Assertions.assertThat
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.io.File
import java.util.Random
import kotlin.experimental.inv

@MediumTest
internal class EncryptionTest {

    @get:Rule
    val forge = ForgeRule()

    @Before
    fun setUp() = deleteAllSdkFiles()

    @After
    fun tearDown() = deleteAllSdkFiles()

    @Test
    fun must_encrypt_all_data() {
        val targetContext = InstrumentationRegistry.getInstrumentation().targetContext

        val configuration = createSdkConfiguration()

        OpenObserve.setVerbosity(Log.VERBOSE)
        val sdkCore =
            OpenObserve.initialize(targetContext, configuration, TrackingConsent.PENDING)
        checkNotNull(sdkCore)
        val featureActivations = mutableListOf(
            {
                val rumConfig =
                    RumConfiguration.Builder(applicationId = forge.anAlphaNumericalString()).build()
                Rum.enable(rumConfig, sdkCore)
            },
            { Logs.enable(LogsConfiguration.Builder().build(), sdkCore) },
            { Trace.enable(TraceConfiguration.Builder().build(), sdkCore) },
            {
                val sessionReplayConfiguration = SessionReplayConfiguration
                    .Builder(100f)
                    .build()
                SessionReplay.enable(sessionReplayConfiguration, sdkCore)
            }
        )
        featureActivations.shuffled(Random(forge.seed)).forEach { it() }

        val tracer = OpenObserveTracing.newTracerBuilder(sdkCore).setBundleWithRumEnabled(true).build()
        GlobalOpenObserveTracer.registerIfAbsent(tracer)

        val logger = Logger.Builder(sdkCore)
            .setBundleWithRumEnabled(true)
            .setBundleWithTraceEnabled(true)
            .build()

        sendEventsForAllFeatures(GlobalRumMonitor.get(sdkCore), logger, tracer)

        flushAndShutdownExecutors()
        stopSdk()

        // this part is flaky, because we make an assumption on the internal folder/file structure
        val isPendingDirectory = { file: File ->
            file.name.contains("pending")
        }
        val isNdkPendingDirectory = { file: File ->
            file.name == "ndk_crash_reports_intermediary_v2"
        }

        val dataDirectories =
            targetContext.cacheDir.listFiles()
                ?.filter { it.isDirectory && it.name.startsWith("datadog") }
                ?.flatMap { it.listFiles()?.asIterable() ?: emptyList() }
                ?.filter {
                    if (it.isDirectory) {
                        (isPendingDirectory(it) && !it.name.contains("crash")) ||
                            isNdkPendingDirectory(it)
                    } else {
                        false
                    }
                }!!

        assertThat(dataDirectories).isNotEmpty()

        dataDirectories.forEach { directory ->

            val files = directory.listFiles()!!

            assertThat(files)
                .overridingErrorMessage("Expecting ${directory.path} to contain files")
                .isNotEmpty

            files.forEach { file ->
                val content = file.readText()

                assertThat(content)
                    .overridingErrorMessage("Expecting ${file.path} to not contain plain text")
                    .doesNotContain("source")
                assertThat(content)
                    .overridingErrorMessage("Expecting ${file.path} to contain encryption marker")
                    .contains(ENCRYPTION_MARKER)
            }
        }
    }

    // region private

    private fun createSdkConfiguration(): Configuration {
        val encryption = object : Encryption {
            override fun encrypt(data: ByteArray): ByteArray {
                return ENCRYPTION_MARKER.toByteArray() + data.map { it.inv() }
            }

            override fun decrypt(data: ByteArray): ByteArray {
                throw UnsupportedOperationException("Shouldn't be called in this test")
            }
        }

        return Configuration
            .Builder(
                clientToken = forge.anAlphaNumericalString(),
                env = forge.anAlphaNumericalString()
            )
            .setEncryption(encryption)
            .build()
    }

    private fun sendEventsForAllFeatures(rumMonitor: RumMonitor, logger: Logger, tracer: OpenObserveTracer) {
        val viewName = "rumView-${forge.aString()}"
        rumMonitor.startView(viewName, viewName)

        logger.w("Started a view")
        val resourceName = "rumResource-${forge.aString()}"

        val span = tracer.buildSpan(resourceName)
            .start()

        rumMonitor.startResource(
            resourceName,
            RumResourceMethod.GET,
            "https://${forge.anAlphaNumericalString()}.com"
        )

        rumMonitor.addAction(
            RumActionType.CUSTOM,
            "rumAction-${forge.aString()}"
        )
        logger.w("Action added")

        rumMonitor.stopResource(
            resourceName,
            forge.anInt(100, 600),
            forge.aLong(min = 0L),
            forge.aValueFrom(RumResourceKind::class.java)
        )

        span.finish()

        rumMonitor.stopView(viewName)
    }

    private fun deleteAllSdkFiles() {
        val targetContext = InstrumentationRegistry.getInstrumentation().targetContext
        targetContext.cacheDir.listFiles()?.forEach {
            it.deleteRecursively()
        }
    }

    private fun stopSdk() {
        OpenObserve.stopInstance()
        GlobalOpenObserveTracer.clear()
    }

    private fun flushAndShutdownExecutors() {
        invokeOpenObserveMethod("flushAndShutdownExecutors")
    }

    private fun invokeOpenObserveMethod(method: String, vararg arguments: Any?) {
        val instance = OpenObserve.javaClass.getDeclaredField("INSTANCE")
        instance.isAccessible = true
        val callMethod = OpenObserve.javaClass.declaredMethods.first { it.name.startsWith(method) }
        callMethod.isAccessible = true
        callMethod.invoke(instance.get(null), *arguments)
    }

    // endregion

    companion object {
        const val ENCRYPTION_MARKER = "~ENCRYPTION-MARKER~"
    }
}
