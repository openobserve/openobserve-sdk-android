/*
 * Unless explicitly stated otherwise all files in this repository are licensed under the Apache License Version 2.0.
 * This product includes software developed at OpenObserve (https://www.openobserve.ai/).
 * Copyright 2016-Present OpenObserve, Inc.
 */

package com.openobserve.android.rum.internal.collections

import com.openobserve.android.internal.collections.EvictingQueue
import java.util.Queue

fun <E> List<E>.toEvictingQueue(maxSize: Int = size * 2): Queue<E> {
    val queue = EvictingQueue<E>(maxSize)
    forEach { queue.add(it) }
    return queue
}
