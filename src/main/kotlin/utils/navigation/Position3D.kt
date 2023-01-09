package utils.navigation

import kotlin.math.abs

data class Position3D(val x: Int, val y: Int, val z: Int) {

    val manhattenDistance = abs(x) + abs(y) + abs(z)

    operator fun plus(other: Position3D) = Position3D(x + other.x, y + other.y, z + other.z)

    operator fun minus(other: Position3D) = this + other * -1

    operator fun times(factor: Int) = Position3D(x * factor, y * factor, z * factor)

    fun getNeighbours() = sequence {
        val range = -1..1
        range.forEach { x ->
            range.forEach { y ->
                range.forEach { z ->
                    if ((x == 0 && y == 0 && z == 0).not()) yield(this@Position3D + Position3D(x, y, z))
                }
            }
        }
    }

    fun getPathThrough(position: Position3D) = generateSequence(this + position) { it + position }

    companion object {
        val origin = Position3D(0, 0, 0)
        val xPlus = Position3D(1, 0, 0)
        val yPlus = Position3D(0, 1, 0)
        val zPlus = Position3D(0, 0, 1)
        val xMinus = xPlus * -1
        val yMinus = yPlus * -1
        val zMinus = zPlus * -1

        val allDirections = listOf(xPlus, xMinus, yPlus, yMinus, zPlus, zMinus)
    }
}