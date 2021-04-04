@file:Suppress("UNUSED_PARAMETER")

package cases.nullable

class NullableLambda {
    fun intToUnit(callback: (Int?) -> Unit) {}
    fun intToInt(callback: (Int?) -> Int) {}
    fun allOptional(lambda: ((Int?) -> String?)?) {}
}