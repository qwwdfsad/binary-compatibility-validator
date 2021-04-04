@file:Suppress("UNUSED_PARAMETER")
package cases.default

internal fun allDefaults(par1: Any = 1, par2: String = "") {}

internal fun someDefaults(par1: Any, par2: String = "") {}


open class ClassFunctions {

    internal open fun allDefaults(par1: Any = 1, par2: String = "") {}

    internal open fun someDefaults(par1: Any, par2: String = "") {}

}


interface InterfaceFunctions {

    fun withAllDefaults(par1: Int = 1, par2: String = "")

    fun withSomeDefaults(par1: Int, par2: String = "")

}