package utils
/*
* x is the column
* y is the row
* y goes "up"
* 
* */
data class Field<T>(val field: List<List<T>>) {

    constructor(x: Int, y: Int, element: () -> T) : this(List(x * y) { element.invoke() }.chunked(y))

    val numberOfRows = field.size
    val numberOfCols = field.first().size

    val rowIndices = 0 until numberOfRows
    val colIndices = 0 until numberOfCols

    operator fun get(position: Position) = field[position.y][position.x]

    fun <T> search(element: T) = sequence {
        (0 until numberOfRows).flatMap { y ->
            (0 until numberOfCols).map { x ->
                if (field[y][x] == element) yield(Position(x, y))
            }
        }
    }


    fun <T> insertAt(position: Position, element: T): Field<T> {
        return field.mapIndexed { y, rows ->
            rows.mapIndexed { x, cell ->
                if(x == position.x && y == position.y) element else cell as T
            }
        }.toField()
    }

    fun <T> insertAt(positionMap: Map<Position,T>): Field<T> {
        return field.mapIndexed { y, rows ->
            rows.mapIndexed { x, cell ->
                if(positionMap.containsKey(Position(x, y))) positionMap[Position(x, y)] as T else cell as T
            }
        }.toField()
    }

    override fun toString() = field
        .joinToString("\n") { row ->
            row.joinToString(" ")
        }
}
