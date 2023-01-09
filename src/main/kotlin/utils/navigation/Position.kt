package utils

import utils.navigation.Direction4
import utils.navigation.Direction8
import kotlin.math.abs

fun main() {
    val origin = Position(0, 0)

    origin.get8Neighbours().toList().print()
    //    [Position(x=0, y=1), Position(x=1, y=1), Position(x=1, y=0), Position(x=1, y=-1), Position(x=0, y=-1), Position(x=-1, y=-1), Position(x=-1, y=0), Position(x=-1, y=1)]

    origin.doMovement(Direction4.North).print()
    //    Position(x=0, y=1)

    origin.doMovement(Direction8.NorthEast).print()
    //    Position(x=1, y=1)
}

data class Position(val x: Int, val y: Int) {
    val manhattenDistance = abs(x) + abs(y)

    operator fun plus(other: Position) = Position(x + other.x, y + other.y)

    operator fun minus(other: Position) = this + other * -1

    operator fun times(factor: Int) = Position(x * factor, y * factor)

    fun doMovement(direction: Direction4, northUp: Boolean = true): Position {
        val step = if(northUp) 1 else -1
        return when (direction) {
            Direction4.North -> Position(x, y + step)
            Direction4.South -> Position(x, y + (-1 * step))
            Direction4.East -> Position(x + 1, y)
            Direction4.West -> Position(x - 1, y)
        }
    }

    fun doMovement(direction: Direction8): Position {
        return when (direction) {
            Direction8.North -> Position(x, y + 1)
            Direction8.NorthEast -> Position(x + 1, y + 1)
            Direction8.East -> Position(x + 1, y)
            Direction8.SouthEast -> Position(x + 1, y - 1)
            Direction8.South -> Position(x, y - 1)
            Direction8.SouthWest -> Position(x - 1, y - 1)
            Direction8.West -> Position(x - 1, y)
            Direction8.NorthWest -> Position(x - 1, y + 1)
        }
    }

    fun get4Neighbours(): Sequence<Position> = sequence {
        Direction4.values().forEach { yield(this@Position.doMovement(it)) }
    }

    fun get8Neighbours(): Sequence<Position> = sequence {
        Direction8.values().forEach { yield(this@Position.doMovement(it)) }
    }

    fun getPathThrough(position: Position) = generateSequence(this) { it + position }

    companion object {
        val origin = Position(0, 0)
        val up = Position(0, 1)
        val right = Position(1, 0)
        val left = Position(-1, 0)
        val down = Position(0, -1)
    }
}

fun Pair<Position, Position>.getLine(): List<Position> {
    val p1 = this.first
    val p2 = this.second
    if (p1.x != p2.x && p1.y != p2.y) throw NotImplementedError()
    return if (p1.x == p2.x) {
        val range = (if (p1.y <= p2.y) (p1.y..p2.y) else (p1.y downTo p2.y))
        range.map { y -> Position(p1.x, y) }
    } else {
        val range = (if (p1.x <= p2.x) (p1.x..p2.x) else (p1.x downTo p2.x))
        range.map { x -> Position(x, p1.y) }
    }
}

fun String.toPosition(): Position {
    val (x, y) = this.split(",")
        .map {
            if (it.contains("="))
                it.split("=").last()
            else
                it

        }
        .map(String::toInt)
    return Position(x, y)
}
