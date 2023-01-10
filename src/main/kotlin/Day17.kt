import utils.*
import kotlin.math.abs

fun main() {
    Day17(IO.TYPE.SAMPLE).test(45)
    Day17().solve()
}

class Day17(inputType: IO.TYPE = IO.TYPE.INPUT) : Day("Trick Shot", inputType = inputType) {

    private val targetPoints = input.parseCornerPoints()
    private val target = targetPoints.toTarget()

    override fun part1(): Any? {
        val maxX = targetPoints[1]
        val maxY = abs(targetPoints[2])
        
        val speeds = (1..maxX).flatMap { x->
            (1..maxY).map { y->
                Position(x,y)
            }
        }
        
        speeds.size.print()
        
        var highest = 0
        var counter = 0
        loop@for (speed in speeds) {
            counter++.print()
            var speed = speed
            var probe = Position.origin
            val path = mutableListOf<Position>()

            while (probe !in target) {
                if (probe.x > maxX || probe.y < targetPoints[2]) {
                    path.clear()
                    continue@loop
                }
                probe += speed
                path.add(probe)
                val dx = if (speed.x == 0) 0 else speed.x - 1
                val dy = speed.y - 1
                speed = Position(dx, dy)
            }

            val max = path.maxOfOrNull { it.y }
            if (max != null && max > highest) {
                highest = max
            }
        }
return highest.print()
        

        return "not yet implement"
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
           