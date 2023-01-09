package utils.navigation

enum class Rotation {
    Left, Right;

    companion object {
        fun of(string: String) = when (string) {
            "R" -> Right
            "L" -> Left
            else -> throw Exception("unknown direction: $string")
        }
    }
}