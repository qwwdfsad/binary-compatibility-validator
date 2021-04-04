@file:Suppress("UNUSED_PARAMETER")

package cases.nullable

class NullableParameters {
    fun objectType(sure: String, maybe: String?) {}
    fun optionalFirst(i: Int?, j: Int) {}
    fun optionalSecond(i: Int, j: Int?) {}
    fun optionalFirstAndLast(i: Int?, j: Int, k: Int?) {}
}