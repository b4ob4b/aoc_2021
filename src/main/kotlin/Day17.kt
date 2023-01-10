import utils.*

fun main() {
    Day17(IO.TYPE.SAMPLE).test(45, 112)
    Day17().solve()
}

class Day17(inputType: IO.TYPE = IO.TYPE.INPUT) : Day("Trick Shot", inputType = inputType) {

    private val cornerPoints = input.parseCornerPoints()
    private val target = cornerPoints.toTarget()

    override fun part1(): Int {
        val speeds = generateSpeeds()
        val paths = speeds.mapNotNull { speed -> speed.pathToTarget() }
        return paths.maxOf { trajectory -> trajectory.maxOf { it.y } }
    }


    override fun part2(): Int {
        val speeds = generateSpeeds()
        return speeds.count { speed -> speed.pathToTarget() != null }
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

    private fun generateSpeeds() = (0..cornerPoints[1]).flatMap { x ->
        (cornerPoints[2]..128).map { y ->
            Position(x, y)
        }
    }

    private fun Position.pathToTarget(): List<Position>? { 
        val path = Position.origin.generateTrajectory(this)
            .takeWhile { (position, _) ->
                position.x <= cornerPoints[1] && position.y >= cornerPoints[2]
            }.toList()

        val last = path.last()

        return if (last.first !in target && (last.first + last.second) !in target) null
        else path.map { it.first }
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