package utils.matrix

import utils.navigation.Direction4
import kotlin.math.abs

data class Position(val row: Int, val col: Int) {

    val manhattenDistance = abs(row) + abs(col)

    operator fun plus(other: Position) = Position(row + other.row, col + other.col)

    operator fun minus(other: Position) = this + other * -1

    operator fun times(factor: Int) = Position(row * factor, col * factor)

    fun moveTo(direction: Direction4): Position {
        return when (direction) {
            Direction4.North -> Position(row - 1, col)
            Direction4.South -> Position(row + 1, col)
            Direction4.East -> Position(row, col + 1)
            Direction4.West -> Position(row, col - 1)
        }
    }

    fun get4Neighbours(): Sequence<Position> = sequence {
        Direction4.values().forEach { yield(this@Position.moveTo(it)) }
    }
}