package com.example

import com.openobserve.tools.annotation.NoOpImplementation

interface RootInterface {
    fun rootMethod()

    val immutableProperty: String
}

interface ParentInterface : RootInterface {
    fun parentMethod()
    var mutableProperty: String
}

@NoOpImplementation
interface InheritedInterface : ParentInterface {
    fun doSomething()
}
