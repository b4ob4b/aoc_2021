import utils.*
import utils.navigation.Position3D

fun main() {
    Day19(IO.TYPE.SAMPLE).test(79, 3621)
    Day19().solve()
}


class Day19(inputType: IO.TYPE = IO.TYPE.INPUT) : Day("", inputType = inputType) {

    private val scanners = input.parseScanners()
    private val scannerPositions = mutableListOf<Position3D>()

    override fun part1(): Int {
        val translatedBeacons = scanners.first().beacons.toMutableSet()
        val scannerStack = ArrayDeque<Scanner>()
            .also {
                it.addAll(scanners.drop(1))
            }

        while (scannerStack.isNotEmpty()) {
            val scanner = scannerStack.removeFirst()

            val beacons = scanner.beacons
            val pairs = translatedBeacons.findPairs(beacons).take(2).toList()

            if (pairs.isEmpty()) {
                scannerStack.addLast(scanner)
                continue
            }

            val delta0 = pairs[0].first - pairs[1].first
            val delta1 = pairs[0].second - pairs[1].second

            val rotate = pointRotations().firstOrNull { rotate ->
                delta0 == rotate(delta1)
            }
            if (rotate == null) {
                scannerStack.addLast(scanner)
                continue
            }

            val offset = pairs.first().let { (p1, p2) ->
                p1 - rotate(p2)
            }
            scannerPositions.add(offset)

            val translations = beacons.map { rotate(it) + offset }
            translatedBeacons.addAll(translations)
        }
        return translatedBeacons.size
    }

    override fun part2(): Int {
        return scannerPositions.combinations(2).map { it[0] - it[1] }.maxOf { it.manhattenDistance }
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

    private fun Collection<Position3D>.findPairs(other: List<Position3D>): Sequence<Pair<Position3D, Position3D>> = sequence {
        val beaconToManhatten = this@findPairs.associateWith { p -> this@findPairs.map { p - it }.map { it.manhattenDistance } }
        val otherBeaconToManhatten = other.associateWith { p -> other.map { p - it }.map { it.manhattenDistance } }
        beaconToManhatten.forEach { (p0, distances0) ->
            otherBeaconToManhatten.forEach { (p1, distances1) ->
                val intersections = distances0 intersect distances1.toSet()
                if (intersections.size >= 10)
                    yield(p0 to p1)
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