/*
 * Unless explicitly stated otherwise all files in this repository are licensed under the Apache License Version 2.0.
 * This product includes software developed at Datadog (https://www.datadoghq.com/).
 * Copyright 2016-Present Datadog, Inc.
 */

package com.openobserve.android

/**
 * Defines the OpenObserve sites you can send tracked data to.
 *
 * @param siteName Explicit site name property introduced in order to have a consistent SDK
 * instance ID (because this value is used there) in case if enum values are renamed.
 * @param intakeHostName the host name for the given site.
 */
enum class OpenObserveSite private constructor(internal val siteName: String, private val intakeHostName: String) {

    /**
     *  The US1 site: [app.openobserve.ai](https://app.openobserve.ai).
     */
    US1("us1", "browser-intake-openobserve.ai"),

    /**
     *  The US3 site: [us3.openobserve.ai](https://us3.openobserve.ai).
     */
    US3("us3"),

    /**
     *  The US5 site: [us5.openobserve.ai](https://us5.openobserve.ai).
     */
    US5("us5"),

    /**
     *  The EU1 site: [app.openobserve.eu](https://app.openobserve.eu).
     */
    EU1("eu1", "browser-intake-openobserve.eu"),

    /**
     *  The AP1 site: [ap1.openobserve.ai](https://ap1.openobserve.ai).
     */
    AP1("ap1"),

    /**
     *  The AP2 site: [ap2.openobserve.ai](https://ap2.openobserve.ai).
     */
    AP2("ap2"),

    /**
     *  The US1_FED site (FedRAMP compatible): [app.openobserve-gov.com](https://app.openobserve-gov.com).
     */
    US1_FED("us1_fed", "browser-intake-openobserve-gov.com"),

    /**
     *  The US2_FED site (FedRAMP compatible): [us2.openobserve-gov.com](https://us2.openobserve-gov.com).
     */
    US2_FED("us2_fed", "browser-intake-us2-openobserve-gov.com"),

    /**
     *  The STAGING site (internal usage only): [app.openobserve.com](https://app.openobserve.com).
     */
    STAGING("staging", "browser-intake-openobserve.com");

    /**
     * Constructor using the generic way to build the intake endpoint host from the site name.
     * @param siteName Explicit site name property introduced in order to have a consistent SDK
     * instance ID (because this value is used there) in case if enum values are renamed.
     */
    private constructor(siteName: String) : this(
        siteName,
        "browser-intake-$siteName-openobserve.ai"
    )

    /** The intake endpoint url. */
    val intakeEndpoint: String = "https://$intakeHostName"
}
