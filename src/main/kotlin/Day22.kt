import utils.*
import utils.navigation.Position3D

fun main() {
    Day22(IO.TYPE.SAMPLE2).test(590784)
    Day22().solve()
}

class Day22(inputType: IO.TYPE = IO.TYPE.INPUT) : Day("Reactor Reboot", inputType = inputType) {

    private val steps = input
        .splitLines()
        .map {
            val state = State.valueOf(it.split(" ").first())
            val ranges = it.to3DRange()
            RebootStep(state, ranges)
        }

    override fun part1(): Int {

        val validRegion = listOf(
            -50..50,
            -50..50,
            -50..50,
        )

        return steps
            .filter { it overlaps validRegion }
            .fold(emptySet<Position3D>()) { acc, (state, ranges) ->
                when (state) {
                    State.on -> acc + ranges.toPositions()
                    State.off -> acc - ranges.toPositions()
                }
            }.size
    }

    override fun part2(): Int {
        return -1
    }

    enum class State { on, off }

    data class RebootStep(val state: State, val ranges: List<IntRange>) {
        infix fun overlaps(region: List<IntRange>): Boolean {
            val pairs = ranges.zip(region)
            return pairs.all { (first, second) ->
                first anyContainedIn second || second anyContainedIn first
            }
        }

        private infix fun IntRange.anyContainedIn(other: IntRange) =
            this.any { other.contains(it) }
    }

    private fun String.to3DRange(): List<IntRange> {
        return """(-?)\d+..(-?)\d+""".toRegex().findAll(this)
            .map { matchResult -> matchResult.value }
            .toList()
            .map { it.asRange() }
    }

    private fun List<IntRange>.toPositions(): Set<Position3D> {
        return this
            .let { (xRange, yRange, zRange) ->
                xRange.flatMap { x ->
                    yRange.flatMap { y ->
                        zRange.map { z ->
                            Position3D(x, y, z)
                        }
                    }
                }
            }.toSet()
    }

    private fun String.asRange() = this.split("..").let { (p1, p2) -> p1.toInt()..p2.toInt() }
}
           