package com.example

import com.openobserve.tools.annotation.NoOpImplementation

@NoOpImplementation
interface OverloadedInterface {

    @Deprecated("foobar")
    fun doSomething(i: Int)

    fun doSomething(i: String)
}
