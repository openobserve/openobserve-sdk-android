/*
 * Unless explicitly stated otherwise all files in this repository are licensed under the Apache License Version 2.0.
 * This product includes software developed at OpenObserve (https://www.openobserve.ai/).
 * Copyright 2016-Present OpenObserve, Inc.
 */

package com.openobserve.android.sessionreplay

/**
 * Constants for MobileSegment record and wireframe types.
 */

/**
 * MetaRecord type.
 * @see com.openobserve.android.sessionreplay.model.MobileSegment.MobileRecord.MetaRecord
 */
const val RECORD_TYPE_META: Long = 4L

/**
 * FocusRecord type.
 * @see com.openobserve.android.sessionreplay.model.MobileSegment.MobileRecord.FocusRecord
 */
const val RECORD_TYPE_FOCUS: Long = 6L

/**
 * ViewEndRecord type.
 * @see com.openobserve.android.sessionreplay.model.MobileSegment.MobileRecord.ViewEndRecord
 */
const val RECORD_TYPE_VIEW_END: Long = 7L

/**
 * VisualViewportRecord type.
 * @see com.openobserve.android.sessionreplay.model.MobileSegment.MobileRecord.VisualViewportRecord
 */
const val RECORD_TYPE_VISUAL_VIEWPORT: Long = 8L

/**
 * MobileFullSnapshotRecord type.
 * @see com.openobserve.android.sessionreplay.model.MobileSegment.MobileRecord.MobileFullSnapshotRecord
 */
const val RECORD_TYPE_FULL_SNAPSHOT: Long = 10L

/**
 * MobileIncrementalSnapshotRecord type.
 * @see com.openobserve.android.sessionreplay.model.MobileSegment.MobileRecord.MobileIncrementalSnapshotRecord
 */
const val RECORD_TYPE_INCREMENTAL_SNAPSHOT: Long = 11L

/**
 * ShapeWireframe type.
 * @see com.openobserve.android.sessionreplay.model.MobileSegment.Wireframe.ShapeWireframe
 */
const val WIREFRAME_TYPE_SHAPE: String = "shape"

/**
 * TextWireframe type.
 * @see com.openobserve.android.sessionreplay.model.MobileSegment.Wireframe.TextWireframe
 */
const val WIREFRAME_TYPE_TEXT: String = "text"

/**
 * ImageWireframe type.
 * @see com.openobserve.android.sessionreplay.model.MobileSegment.Wireframe.ImageWireframe
 */
const val WIREFRAME_TYPE_IMAGE: String = "image"

/**
 * PlaceholderWireframe type.
 * @see com.openobserve.android.sessionreplay.model.MobileSegment.Wireframe.PlaceholderWireframe
 */
const val WIREFRAME_TYPE_PLACEHOLDER: String = "placeholder"

/**
 * WebviewWireframe type.
 * @see com.openobserve.android.sessionreplay.model.MobileSegment.Wireframe.WebviewWireframe
 */
const val WIREFRAME_TYPE_WEBVIEW: String = "webview"
