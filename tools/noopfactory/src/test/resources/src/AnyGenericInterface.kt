package com.example

import com.openobserve.tools.annotation.NoOpImplementation

@NoOpImplementation
interface AnyGenericInterface<T : Any> {
    fun doSomethingWithListReturn(models: List<T>): List<T>

    fun map(event: T): T

    fun map(event: T, additionalParameters: Map<String, Any?>): T

    val immutableProperty: T?

    var mutableProperty: T?
}
