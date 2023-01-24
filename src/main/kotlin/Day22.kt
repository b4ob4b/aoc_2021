import utils.*

fun main() {
    Day22(IO.TYPE.SAMPLE2).test(590784L)
    Day22(IO.TYPE.SAMPLE).test(part2 = 2758514936282235L)
    Day22().solve()
}

class Day22(inputType: IO.TYPE = IO.TYPE.INPUT) : Day("Reactor Reboot", inputType = inputType) {

    private val steps = input
        .splitLines()
        .map {
            val state = State.valueOf(it.split(" ").first())
            val ranges = it.to3DRange()
            RebootStep(state, Cuboid(ranges))
        }

    override fun part1(): Long {

        val validRegion = Cuboid(
            -50..50,
            -50..50,
            -50..50,
        )

        return steps
            .filter { it.cuboid overlaps validRegion }
            .fold(emptyList<Cuboid>()) { region, (state, cuboid) ->
                when (state) {
                    State.on -> region add cuboid
                    State.off -> region remove cuboid
                }
            }.countCubes()
    }

    override fun part2() = steps.fold(emptyList<Cuboid>()) { region, (state, cuboid) ->
        when (state) {
            State.on -> region add cuboid
            State.off -> region remove cuboid
        }
    }.countCubes()

    private infix fun List<Cuboid>.add(that: Cuboid): List<Cuboid> {
        if (this.isEmpty()) return listOf(that)
        val restOfThat = this.fold(listOf(that)) { thatCuboid, regionPart ->
            thatCuboid.flatMap { it - regionPart }
        }
        return this + restOfThat
    }

    private infix fun List<Cuboid>.remove(that: Cuboid) = this.map { it - that }.flatten()

    private fun List<Cuboid>.countCubes() = this.sumOf { it.countCubes() }

    enum class State { on, off }
    
    data class RebootStep(val state: State, val cuboid: Cuboid)

    private fun String.to3DRange(): List<IntRange> {
        return """(-?)\d+..(-?)\d+""".toRegex().findAll(this)
            .map { matchResult -> matchResult.value }
            .toList()
            .map { it.asRange() }
    }

    private fun String.asRange() = this.split("..").let { (p1, p2) -> p1.toInt()..p2.toInt() }

    data class Cuboid(val ranges: List<IntRange>) {
        constructor(intRangeX: IntRange, intRangeY: IntRange, intRangeZ: IntRange) : this(listOf(intRangeX, intRangeY, intRangeZ))

        infix fun overlaps(other: Cuboid): Boolean {
            val pairs = ranges.zip(other.ranges)
            return pairs.all { it.first anyContainedIn it.second }
        }

        private infix fun IntRange.anyContainedIn(other: IntRange) =
            this.any { other.contains(it) }

        operator fun plus(that: Cuboid): List<Cuboid> {
            val isOverlapping = this overlaps that
            return if (isOverlapping) {
                listOf(this) + (that - this)
            } else {
                listOf(this, that)
            }
        }

        operator fun minus(that: Cuboid): List<Cuboid> {
            val isOverlapping = this overlaps that
            return if (isOverlapping) {
                this subtract that
            } else {
                listOf(this)
            }
        }

        fun countCubes() = ranges.map { it.last.toLong() - it.first + 1L }.product()

        data class Overlap(val overlap: IntRange, val first: List<IntRange>, val second: List<IntRange>)

        private infix fun IntRange.findOverlapsWith(that: IntRange): Overlap {
            val overlap = maxOf(this.first, that.first)..minOf(this.last, that.last)

            return Overlap(
                overlap,
                buildList {
                    if (this@findOverlapsWith.first < overlap.first)
                        add(this@findOverlapsWith.first until overlap.first)
                    if (this@findOverlapsWith.last > overlap.last)
                        add((overlap.last + 1)..this@findOverlapsWith.last)
                },
                buildList {
                    if (that.first < overlap.first)
                        add(that.first until overlap.first)
                    if (that.last > overlap.last)
                        add((overlap.last + 1)..that.last)
                }
            )
        }

        private infix fun findOverlaps(that: Cuboid): List<Overlap> {
            val (xR, yR, zR) = ranges
            val (xO, yO, zO) = that.ranges

            return buildList {
                add(xR findOverlapsWith xO)
                add(yR findOverlapsWith yO)
                add(zR findOverlapsWith zO)
            }
        }

        private infix fun subtract(that: Cuboid): List<Cuboid> {
            val (x, y, z) = this findOverlaps that

            return buildList {
                addAll(x.first.map { Cuboid(it, y.overlap, z.overlap) })
                addAll(y.first.map { Cuboid(x.overlap, it, z.overlap) })
                addAll(z.first.map { Cuboid(x.overlap, y.overlap, it) })

                x.first.map { x -> y.first.map { y -> add(Cuboid(x, y, z.overlap)) } }
                x.first.map { x -> z.first.map { z -> add(Cuboid(x, y.overlap, z)) } }
                y.first.map { y -> z.first.map { z -> add(Cuboid(x.overlap, y, z)) } }

                x.first.map { x ->
                    y.first.map { y ->
                        z.first.map { z -> add(Cuboid(x, y, z)) }
                    }
                }
            }
        }
    }

}
           