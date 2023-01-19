import utils.*
import utils.matrix.Matrix
import utils.matrix.Position

fun main() {
    Day20(IO.TYPE.SAMPLE).test(35)
    Day20().solve()
}

class Day20(inputType: IO.TYPE = IO.TYPE.INPUT) : Day("", inputType = inputType) {
    override fun part1(): Any? {
        val imageEnhancementAlgorithm = input.split("\n\n").first()
        var matrix = input.split("\n\n").last().toGrid().toMatrix()


        val mask = listOf(
            Position(-1, -1), Position(-1, 0), Position(-1, 1),
            Position(0, -1), Position(0, 0), Position(0, 1),
            Position(1, -1), Position(1, 0), Position(1, 1),
        )

        val extension = 3

        repeat(2) {

            val rows = (-extension..matrix.numberOfRows + (extension))
            val cols = (-extension..matrix.numberOfCols + (extension))

            val m = buildMap<Position, String> {
                for (r in rows) {
                    for (c in cols) {
                        val p = Position(r, c)
                        val s = buildString {
                            mask.forEach {
                                val pp = p + it
                                if (matrix contains (pp)) {
                                    append(matrix[pp])
                                } else append(".")
                            }
                        }
                        put(p, s)
                    }
                }
            }


            val output = m.map { (p, pixels) ->
                val number = pixels.toBinary().binaryToDecimal()
                p to imageEnhancementAlgorithm[number].toString()
            }

            matrix = Matrix(rows.last + extension, cols.last + extension) { "." }.insertAt(output.associate { (p, s) ->
                p + Position(
                    extension,
                    extension
                ) to s
            })
        }
        val oneHalfExtension = extension + extension / 2
        val rowIndices = oneHalfExtension..(matrix.numberOfRows - oneHalfExtension)
        val colIndices = oneHalfExtension..(matrix.numberOfCols - oneHalfExtension)

        return rowIndices.sumOf { r ->
            colIndices.count { c ->
                matrix[Position(r, c)] == "#"
            }
        }
    }

    override fun part2(): Any? {
        return "not yet implement"
    }


    private fun String.toBinary() = this.toList().joinToString("") {
        if (it == '#') "1" else "0"
    }
}           