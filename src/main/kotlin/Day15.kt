import utils.*
import utils.matrix.Matrix
import utils.matrix.Position

fun main() {
    Day15(IO.TYPE.SAMPLE).test(40, 315)
    Day15().solve()
}

class Day15(inputType: IO.TYPE = IO.TYPE.INPUT) : Day("Chiton", inputType = inputType) {

    private val field = input.toGrid { it.toInt() }.toMatrix()
    private val smallField = Field(field)
    private val bigField = Field(field, 5)

    override fun part1() = searchSafestPath(smallField)

    override fun part2() = searchSafestPath(bigField)

    data class Field(val field: Matrix<Int>, val size: Int = 1) {
        val goal = Position(field.numberOfRows * size - 1, field.numberOfCols * size - 1)

        operator fun get(position: Position): Int {
            if (field contains position) return field[position]
            val (row, col) = position
            val increment = row / field.numberOfRows + col / field.numberOfCols
            val newRow = if (row > (field.numberOfRows - 1)) row % field.numberOfRows else row
            val newCol = if (col > (field.numberOfCols - 1)) col % field.numberOfCols else col
            val newRisk = (field[Position(newRow, newCol)] + increment) % 9
            return if (newRisk == 0) 9 else newRisk
        }

        infix fun contains(position: Position): Boolean {
            if (size == 1) return field contains position
            if (position.row < 0 || position.col < 0) return false
            return field contains Position(position.row / size, position.col / size)
        }
    }
    
    private fun searchSafestPath(field: Field): Int {
        val queue: MutableSet<Pair<Position, Int>> = mutableSetOf()
        queue.add(Position.origin to 0)
        val seen = mutableMapOf<Position, Int>()
        while (queue.isNotEmpty()) {
            val tuple = queue.toSortedSet(compareBy { (position, distance) ->
                distance + (position - field.goal).manhattenDistance
            }).first()
            queue.remove(tuple)

            val (position, distance) = tuple
            if (seen.containsKey(position) && seen[position]!! > distance) continue
            seen[position] = distance

            if (position == field.goal) {
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
}           