package com.example

import com.openobserve.tools.annotation.NoOpImplementation

@NoOpImplementation(publicNoOpImplementation = true)
interface PublicImplementation {
    fun doSomething()
}
