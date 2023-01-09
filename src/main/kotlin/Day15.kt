import utils.*
import utils.matrix.Position

fun main() {
    Day15(IO.TYPE.SAMPLE).test(40)
    Day15().solve()
}

class Day15(inputType: IO.TYPE = IO.TYPE.INPUT) : Day("", inputType = inputType) {

    private val field = input.toGrid { it.toInt() }.toMatrix()

    override fun part1(): Int {
        val queue: MutableSet<Pair<Position, Int>> = mutableSetOf()
        queue.add(Position.origin to 0)
        val goal = Position(field.numberOfRows - 1, field.numberOfCols - 1)
        val seen = mutableMapOf<Position, Int>()

        while (queue.isNotEmpty()) {
            val tuple = queue.toSortedSet(compareBy { (position, distance) ->
                distance + (position - goal).manhattenDistance
            }).first()

            queue.remove(tuple)

            val (position, distance) = tuple
            if (seen.containsKey(position) && seen[position]!! > distance) continue
            seen[position] = distance

            if (position == goal) {
                return distance
            }

            position.get4Neighbours()
                .filter { field contains it }
                .associateWith { distance + field[it] }
                .filter { (position, distance) ->
                    (seen[position] ?: Int.MAX_VALUE) > distance
                }.forEach {
                    queue.add(it.toPair())
                }

        }
        throw  Exception("nothing found")
    }

    override fun part2(): Any? {
        return "not yet implement"
    }
}           