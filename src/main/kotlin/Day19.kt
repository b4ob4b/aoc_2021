import utils.*
import utils.navigation.Position3D

fun main() {
    Day19(IO.TYPE.SAMPLE).test(79)
    Day19().solve()
}


class Day19(inputType: IO.TYPE = IO.TYPE.INPUT) : Day("", inputType = inputType) {
    private val scanners = input.parseScanners()

    override fun part1(): Int {

        val translatedBeacons = scanners.first().beacons.toMutableSet()
        val scanners = scanners.toMutableList().also { it.removeFirst() }
        val toRemove = mutableSetOf<Scanner>()
        while (scanners.isNotEmpty()) {

            for (s in scanners) {
                val beacons = s.beacons
                val pairs = translatedBeacons.findPairs(beacons)

                if (pairs.isEmpty()) continue

                val d0 = pairs[0].first - pairs[1].first
                val d1 = pairs[0].second - pairs[1].second

                val r = pointRotations().firstOrNull { rotate ->
                    d0 == rotate(d1)
                } ?: continue

                val offset = pairs.first().let { (p1, p2) ->
                    p1 - r(p2)
                }

                val translations = beacons.map { r(it) + offset }
                translatedBeacons.addAll(translations)
                toRemove.add(s)
            }
            scanners.removeAll(toRemove)
        }
        return translatedBeacons.size
    }

    override fun part2(): Any? {
        return "not yet implement"
    }


    data class Scanner(
        val id: Int,
        val beacons: List<Position3D>,
    )

    private fun String.parseScanners() = this.split("\n\n")
        .mapIndexed { index, s ->
            val beacons = s.splitLines().drop(1).map { it.toPosition3D() }
            Scanner(index, beacons)
        }

    private fun Collection<Position3D>.findPairs(other: List<Position3D>): List<Pair<Position3D, Position3D>> {
        val beaconToManhatten = this.associateWith { p -> this.map { p - it }.map { it.manhattenDistance } }
        val otherBeaconToManhatten = other.associateWith { p -> other.map { p - it }.map { it.manhattenDistance } }
        return beaconToManhatten.flatMap { (p0, distances0) ->
            otherBeaconToManhatten.mapNotNull { (p1, distances1) ->
                val intersections = distances0 intersect distances1.toSet()
                if (intersections.size >= 10)
                    p0 to p1
                else null
            }
        }
    }

    private fun pointRotations(): Sequence<(Position3D) -> Position3D> = sequence {
        val minus = buildList {
            add(listOf(1, 1, 1))
            addAll(listOf(1, 1, -1).permutations().toSet())
            addAll(listOf(1, -1, -1).permutations().toSet())
            add(listOf(-1, -1, -1))
        }
        minus.forEach { (m1, m2, m3) ->
            sequenceOf<(Position3D) -> Position3D>(
                { Position3D(it.x, it.y, it.z) },
                { Position3D(it.y, it.x, it.z) },
                { Position3D(it.y, it.z, it.x) },
                { Position3D(it.x, it.z, it.y) },
                { Position3D(it.z, it.x, it.y) },
                { Position3D(it.z, it.y, it.x) },
            ).forEach { rotate ->
                yield { position ->
                    val (x, y, z) = position
                    val p = Position3D(x * m1, y * m2, z * m3)
                    rotate(p)
                }
            }
        }
    }
}