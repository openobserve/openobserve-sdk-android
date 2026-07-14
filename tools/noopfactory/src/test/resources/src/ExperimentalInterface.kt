package com.example

import com.openobserve.tools.annotation.NoOpImplementation

@NoOpImplementation
interface ExperimentalInterface {
    @ExperimentalApi
    fun doSomethingExperimental()
}
