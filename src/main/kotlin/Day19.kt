import utils.*
import utils.navigation.Position3D
import kotlin.math.abs

fun main() {
    Day19(IO.TYPE.SAMPLE2).test(79)
    Day19().solve()
}

class Day19(inputType: IO.TYPE = IO.TYPE.INPUT) : Day("", inputType = inputType) {
    private val scanners = input.split("\n\n")
        .map {
            val beacons = it.splitLines().drop(1).map {
                val (x, y, z) = it.split(",").map { it.toInt() }
                Position3D(x, y, z)
            }
            Scanner(beacons)
        }

    override fun part1(): Any? {

        val s0 = scanners[0]
        val s1 = scanners[1]
        val s2 = scanners[2]
        val s3 = scanners[3]
        val s4 = scanners[4]

        (s1 findRotation s4).print()

        val o1 = (s0 countIntersectingBeacons s1).findOffset().print()
        val o2 = (s0 countIntersectingBeacons s2).findOffset().print()
        val o3 = ((s1 countIntersectingBeacons s3).findOffset().rotate(Rotation.yUp2) + Position3D(x = 68, y = -1246, z = -43)).print()
        val o4 = ((s1 countIntersectingBeacons s4).findOffset().rotate(Rotation.yUp2) + Position3D(x = 68, y = -1246, z = -43)).print()

        buildSet {
            addAll(s0.beacons.filter { it.inRange() })
            addAll(s1.beacons.map { it.rotate(Rotation.yUp2) + o1 })
            addAll(s2.beacons.map { it.rotate(Rotation.zUp2) + o2 })
            addAll(s3.beacons.map { it.rotate(Rotation.yUp0).rotate(Rotation.yUp2) + o3 })
            addAll(s4.beacons.map { it.rotate(Rotation.xUp1Reverse).rotate(Rotation.yUp2) + o4 })
        }.map { it.inRange() }.size.print()
        
        return "not yet implement"
    }

    private fun Position3D.inRange(): Boolean {
        val (x, y, z) = this
        return (abs(x) < 1000 && abs(y) < 1000 && abs(z) < 1000)
    }

    private fun Triple<Pair<Position3D, Position3D>, Rotation, Pair<Position3D, Position3D>>.findOffset(): Position3D {
        val (p1, r, p2) = this
        return p1.first - p2.first.rotate(r)
    }

    data class Relation(val points: Pair<Position3D, Position3D>, val delta: Position3D)

    data class Scanner(val beacons: List<Position3D>) {

        fun getRelationPairs() = sequence {
            val pairs = beacons.combinations(2).iterator()
            while (pairs.hasNext()) {
                val pair = pairs.next().zipWithNext().single()
                yield(Relation(pair, pair.first - pair.second))
            }
        }

        infix fun findRotation(other: Scanner): Rotation {
            return Rotation.values().asSequence().filter { it.name.contains("minus").not() }.first { rotation ->
                other.getRelationPairs().any { (otherPoints, otherDelta) ->
                    getRelationPairs().any { (points, delta) ->
                        rotation.rotate(otherDelta) == delta
                    }
                }
            }
        }

        infix fun findRotation3(other: Scanner): Triple<Pair<Position3D, Position3D>, Rotation, Pair<Position3D, Position3D>> {
            return Rotation.values().asSequence().map { rotation ->
                other.getRelationPairs().map { (otherPoints, otherDelta) ->
                    getRelationPairs().mapNotNull { (points, delta) ->
                        if (rotation.rotate(otherDelta) == delta) Triple(points, rotation, otherPoints) else null
                    }.first()
                }.first()
            }.first()
        }


        fun findRotation2(other: Scanner, rotation: Rotation): Pair<Pair<Position3D, Position3D>, Pair<Position3D, Position3D>> {
            return other.getRelationPairs().map { (otherPoints, otherDelta) ->
                getRelationPairs().mapNotNull { (points, delta) ->
                    if (rotation.rotate(otherDelta) == delta) {
                        points to otherPoints
                    } else null
                }.first()
            }.first()
        }

        infix fun countIntersectingBeacons(other: Scanner): Triple<Pair<Position3D, Position3D>, Rotation, Pair<Position3D, Position3D>> {
            return other.getRelationPairs().map { (s2Points, s2Delta) ->
                this.getRelationPairs().mapNotNull { (points, delta) ->
                    val rotation =
                        Rotation.values().asSequence().filter { it.name.contains("minus").not() }.firstOrNull { it.rotate(s2Delta) == delta }
                    rotation?.let { Triple(points, it, s2Points) }
                }
            }.flatten().first()
        }

        infix fun intersect(other: Scanner): Pair<Set<Position3D>, Set<Position3D>> {
            val x = other.getRelationPairs().flatMap { (s2Points, s2Delta) ->
                this.getRelationPairs().mapNotNull { (points, delta) ->
                    val rotation = Rotation.values().asSequence().firstOrNull { it.rotate(s2Delta) == delta }
                    rotation?.let { points to s2Points }
                }
            }.toList()
            return x.flatMap { (x, y) -> x.toList().toSet() }.toSet() to
                    x.flatMap { (x, y) -> y.toList().toSet() }.toSet()
        }
    }

    private fun Position3D.rotate(rotation: Rotation) = rotation.rotate(this)

    enum class Rotation {
        yUp0, yUp1, yUp2, yUp3,
        zUp0, zUp1, zUp2, zUp3,
        xUp0, xUp1, xUp2, xUp3,
        minusYUp0, minusYUp1, minusYUp2, minusYUp3,
        minusZUp0, minusZUp1, minusZUp2, minusZUp3,
        minusXUp0, minusXUp1, minusXUp2, minusXUp3,

        yUp1Reverse,
        zUp0Reverse,
        zUp3Reverse,
        xUp0Reverse,
        xUp1Reverse,
        minusYUp1Reverse,
        minusZUp1Reverse,
        minusZUp2Reverse,
        minusXUp2Reverse,
        minusXUp3Reverse,
        ;

        val reverse: Rotation
            get() = when (this) {
                yUp0 -> yUp0
                yUp1 -> yUp1Reverse
                yUp2 -> yUp2
                yUp3 -> yUp3
                zUp0 -> zUp0Reverse
                zUp1 -> xUp3
                zUp2 -> zUp2
                zUp3 -> zUp3Reverse
                xUp0 -> xUp0Reverse
                xUp1 -> xUp1Reverse
                xUp2 -> xUp2
                xUp3 -> zUp1
                minusYUp0 -> minusYUp0
                minusYUp1 -> minusYUp1Reverse
                minusYUp2 -> minusYUp2
                minusYUp3 -> minusYUp3
                minusZUp0 -> minusZUp0
                minusZUp1 -> minusZUp1Reverse
                minusZUp2 -> minusZUp2Reverse
                minusZUp3 -> minusXUp1
                minusXUp0 -> minusXUp0
                minusXUp1 -> minusZUp3
                minusXUp2 -> minusXUp2Reverse
                minusXUp3 -> minusXUp3Reverse
                yUp1Reverse -> yUp1
                zUp0Reverse -> zUp0
                zUp3Reverse -> zUp3
                xUp0Reverse -> xUp0
                xUp1Reverse -> xUp1
                minusYUp1Reverse -> minusYUp1
                minusZUp1Reverse -> minusZUp1
                minusZUp2Reverse -> minusZUp2
                minusXUp2Reverse -> minusXUp2
                minusXUp3Reverse -> minusXUp3
            }

        fun reverse(position3D: Position3D): Position3D {
            val (x, y, z) = position3D
            return when (this) {

                zUp0 -> Position3D(x, y, -z)
                else -> TODO()
            }
        }

        fun rotate(position3D: Position3D): Position3D {
            val (x, y, z) = position3D
            return when (this) {
                yUp0 -> Position3D(x, y, z)
                yUp1 -> Position3D(-z, y, x)
                yUp2 -> Position3D(-x, y, -z)
                yUp3 -> Position3D(-z, y, -x)
                zUp0 -> Position3D(x, z, -y)
                zUp1 -> Position3D(y, z, x)
                zUp2 -> Position3D(-x, z, y)
                zUp3 -> Position3D(-y, z, -x)
                xUp0 -> Position3D(-y, x, z)
                xUp1 -> Position3D(-z, x, -y)
                xUp2 -> Position3D(y, x, -z)
                xUp3 -> Position3D(z, x, y)
                minusYUp0 -> Position3D(x, -y, z)
                minusYUp2 -> Position3D(-x, -y, -z)
                minusYUp3 -> Position3D(-z, -y, -x)
                minusZUp0 -> Position3D(x, -z, -y)
                minusZUp1 -> Position3D(y, -z, x)
                minusZUp2 -> Position3D(-x, -z, y)
                minusYUp1 -> Position3D(-z, -y, x)
                minusZUp3 -> Position3D(-y, -z, -x)
                minusXUp0 -> Position3D(-y, -x, z)
                minusXUp1 -> Position3D(-z, -x, -y)
                minusXUp2 -> Position3D(y, -x, -z)
                minusXUp3 -> Position3D(z, -x, y)
                yUp1Reverse -> Position3D(z, y, -x)
                zUp0Reverse -> Position3D(x, -z, y)
                zUp3Reverse -> Position3D(-z, -x, y)
                xUp0Reverse -> Position3D(y, -x, z)
                xUp1Reverse -> Position3D(y, -z, -x)
                minusYUp1Reverse -> Position3D(z, -y, -x)
                minusZUp1Reverse -> Position3D(z, x, -y)
                minusZUp2Reverse -> Position3D(-x, z, -y)
                minusXUp2Reverse -> Position3D(-y, x, -z)
                minusXUp3Reverse -> Position3D(-y, z, x)
            }
        }
    }


    override fun part2(): Any? {
        return "not yet implement"
    }
}
      