package utils.navigation

enum class Direction4 {
    North, East, South, West;

    fun rotateBy(rotation: Rotation) = when(rotation) {
        Rotation.Left -> when(this) {
            North -> West
            East -> North
            South -> East
            West -> South
        }
        Rotation.Right -> when(this) {
            North -> East
            East -> South
            South -> West
            West -> North
        }
    }
}

enum class Direction8 {
    North, NorthEast, East, SouthEast, South, SouthWest, West, NorthWest
}
