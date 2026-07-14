/*
 * Unless explicitly stated otherwise all files in this repository are licensed under the Apache License Version 2.0.
 * This product includes software developed at OpenObserve (https://www.openobserve.ai/).
 * Copyright 2016-Present OpenObserve, Inc.
 */

package com.openobserve.android.core.internal.persistence.file.advanced

import com.openobserve.tools.annotation.NoOpImplementation

/**
 * A [Runnable] used to perform a data migration operation (moving, modifying or deleting files).
 */
@NoOpImplementation
internal interface DataMigrationOperation : Runnable
