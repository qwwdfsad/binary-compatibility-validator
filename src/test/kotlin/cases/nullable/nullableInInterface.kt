package cases.nullable

interface Nullable {
    var objectType: String?
    var basicType: Int?

    fun operation(i: Int?): String?
}