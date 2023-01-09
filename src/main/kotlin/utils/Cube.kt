package utils

import utils.navigation.Position3D

data class Cube(val corner: Position3D = Position3D.origin) {
    
    private val defaultCorners = listOf(
        Position3D(0, 0, 0),
        Position3D(1, 0, 0),
        Position3D(0, 1, 0),
        Position3D(0, 0, 1),
        Position3D(1, 1, 0),
        Position3D(1, 0, 1),
        Position3D(0, 1, 1),
        Position3D(1, 1, 1),
    )

    private val defaultAreas = listOf(
        setOf(
            Position3D(0, 0, 0),
            Position3D(1, 0, 0),
            Position3D(0, 1, 0),
            Position3D(1, 1, 0),
        ),
        setOf(
            Position3D(0, 0, 0),
            Position3D(1, 0, 0),
            Position3D(0, 0, 1),
            Position3D(1, 0, 1),
        ),
        setOf(
            Position3D(0, 0, 0),
            Position3D(0, 0, 1),
            Position3D(0, 1, 0),
            Position3D(0, 1, 1),
        ),
        setOf(
            Position3D(0, 0, 1),
            Position3D(1, 0, 1),
            Position3D(0, 1, 1),
            Position3D(1, 1, 1),
        ),
        setOf(
            Position3D(1, 0, 0),
            Position3D(1, 1, 0),
            Position3D(1, 0, 1),
            Position3D(1, 1, 1),
        ),
        setOf(
            Position3D(0, 1, 0),
            Position3D(1, 1, 0),
            Position3D(0, 1, 1),
            Position3D(1, 1, 1),
        ),
    )

    val corners = defaultCorners.map { it + corner }

    val areas = defaultAreas.map { side -> side.map { it + corner }.toSet() }

    operator fun plus(position3D: Position3D) = Cube(position3D + corner)

    operator fun minus(position3D: Position3D) = Cube(position3D * -1 + corner)

    override fun toString(): String {
        return corner.let { (x, y, z) -> "Cube($x,$y,$z)" }
    }
}