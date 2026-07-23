/*
 * Unless explicitly stated otherwise all files in this repository are licensed under the Apache License Version 2.0.
 * This product includes software developed at Datadog (https://www.datadoghq.com/).
 * Copyright 2016-Present Datadog, Inc.
 */

package com.openobserve.gradle.config

import com.android.build.api.dsl.LibraryExtension
import org.gradle.api.Project
import org.gradle.api.publish.PublishingExtension
import org.gradle.api.publish.maven.MavenPublication
import org.gradle.kotlin.dsl.create
import org.gradle.kotlin.dsl.findByType
import org.gradle.plugins.signing.SigningExtension

object MavenConfig {
    const val GROUP_ID = "ai.openobserve"
    const val PUBLICATION = "release"

    private const val ARTIFACT_PREFIX = "o2-sdk-android-"
    private const val DISPLAY_PREFIX = "OpenObserve SDK Android"

    /**
     * Tokens that must not be naively capitalized. Anything absent from this map is
     * title-cased, so a new module needs an entry here only when its name is an
     * acronym or has interior capitals.
     */
    private val ACRONYMS = mapOf(
        "rum" to "RUM",
        "ndk" to "NDK",
        "api" to "API",
        "tv" to "TV",
        "rx" to "Rx",
        "otel" to "OTel",
        "okhttp" to "OkHttp",
        "webview" to "WebView",
        "openfeature" to "OpenFeature",
        "sqldelight" to "SQLDelight",
        "coil3" to "Coil3"
    )

    /**
     * Human-readable POM `<name>`, e.g. `o2-sdk-android-internal` ->
     * "OpenObserve SDK Android Internal".
     *
     * Upstream set `<name>` to the raw Gradle module name, so Maven Central rendered the
     * artifactId as the display name. Deriving the label from the artifactId rather than
     * hardcoding a table per module means the two cannot drift apart on a rename.
     */
    fun displayName(artifactId: String): String {
        val suffix = artifactId.removePrefix(ARTIFACT_PREFIX)
            .split('-')
            .filter { it.isNotEmpty() }
            .joinToString(" ") { token ->
                ACRONYMS[token] ?: token.replaceFirstChar { it.uppercase() }
            }
        return if (suffix.isEmpty()) DISPLAY_PREFIX else "$DISPLAY_PREFIX $suffix"
    }
}

fun Project.publishingConfig(
    projectDescription: String,
    customArtifactId: String = name
) {
    val projectName = name

    val androidExtension =
        extensions.findByType(LibraryExtension::class.java)
    if (androidExtension == null) {
        logger.error("Missing android library extension for $projectName")
        return
    }

    androidExtension.publishing {
        singleVariant(MavenConfig.PUBLICATION) {
            withSourcesJar()
            withJavadocJar()
        }
    }

    afterEvaluate {
        val publishingExtension = extensions.findByType(PublishingExtension::class)
        val signingExtension = extensions.findByType(SigningExtension::class)
        if (publishingExtension == null || signingExtension == null) {
            logger.error("Missing publishing or signing extension for $projectName")
            return@afterEvaluate
        }

        publishingExtension.apply {
            publications.create<MavenPublication>(MavenConfig.PUBLICATION) {
                from(components.getByName("release"))

                groupId = MavenConfig.GROUP_ID
                artifactId = customArtifactId
                version = AndroidConfig.VERSION.name

                pom {
                    // Derived from the artifactId (what is actually published), not the Gradle
                    // module name, so a module whose artifactId is overridden still gets a label
                    // matching the coordinate consumers resolve.
                    name.set(MavenConfig.displayName(customArtifactId))
                    description.set(projectDescription)
                    url.set("https://github.com/openobserve/openobserve-sdk-android/")

                    licenses {
                        license {
                            name.set("Apache-2.0")
                            url.set("https://www.apache.org/licenses/LICENSE-2.0")
                        }
                    }

                    organization {
                        name.set("OpenObserve")
                        url.set("https://www.openobserve.ai/")
                    }

                    developers {
                        developer {
                            name.set("OpenObserve")
                            email.set("info@openobserve.ai")
                            organization.set("OpenObserve")
                            organizationUrl.set("https://www.openobserve.ai/")
                        }
                    }

                    scm {
                        url.set("https://github.com/openobserve/openobserve-sdk-android/")
                        connection.set("scm:git:git@github.com:OpenObserve/o2-sdk-android.git")
                        developerConnection.set("scm:git:git@github.com:OpenObserve/o2-sdk-android.git")
                    }
                }
            }
        }

        signingExtension.apply {
            val privateKey = System.getenv("GPG_PRIVATE_KEY")
            val password = System.getenv("GPG_PASSWORD")
            isRequired = !hasProperty("oo-skip-signing")
            useInMemoryPgpKeys(privateKey, password)
            sign(publishingExtension.publications.getByName(MavenConfig.PUBLICATION))
        }
    }
}
