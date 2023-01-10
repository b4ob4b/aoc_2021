import utils.*
import kotlin.math.abs

fun main() {
    Day17(IO.TYPE.SAMPLE).test(45)
    Day17().solve()
}

class Day17(inputType: IO.TYPE = IO.TYPE.INPUT) : Day("Trick Shot", inputType = inputType) {

    private val cornerPoints = input.parseCornerPoints()
    private val target = cornerPoints.toTarget()

    override fun part1(): Int {
        val maxX = cornerPoints[1]
        val maxY = cornerPoints[2]
        val speeds = (1..cornerPoints[1]).flatMap { x ->
            (1..abs(maxY)).map { y ->
                Position(x, y)
            }
        }

        return speeds.maxOf { speed ->
            val path = Position.origin.generateTrajectory(speed).takeWhile { 
                    (position, _) -> position.x < maxX && position.y > maxY
            }
            
            val last = path.last().let { it.first + it.second }
            if (last in target) path.maxOf { it.first.y } else 0
        }
    }

    private fun Position.generateTrajectory(initialSpeed: Position) = generateSequence(this to initialSpeed) {
        val position = it.first
        val speed = it.second
        (position + speed) to speed.alterSpeed()
    }

    private fun Position.alterSpeed(): Position {
        return Position(
            if (this.x == 0) 0 else this.x - 1,
            this.y - 1
        )
    }

    override fun part2(): Any? {
        return "not yet implement"
    }

    private fun String.parseCornerPoints() = """-?\d+""".toRegex().findAll(this).take(4).map { it.value.toInt() }.toList()
    private fun List<Int>.toTarget(): List<Position> {
        val (x1, x2, y1, y2) = this
        return (x1..x2).flatMap { x ->
            (y1..y2).map { y ->
                Position(x, y)
            }
        }
    }
}
           