import utils.*
import utils.matrix.Matrix
import utils.matrix.Position

fun main() {
    Day20(IO.TYPE.SAMPLE).test(35, 3351)
    Day20().solve()
}

class Day20(inputType: IO.TYPE = IO.TYPE.INPUT) : Day("Trench Map", inputType = inputType) {

    private val imageEnhancementAlgorithm = input.split("\n\n").first()
    private val matrix = input.split("\n\n").last().toGrid().toMatrix()

    private val mask = listOf(
        Position(-1, -1), Position(-1, 0), Position(-1, 1),
        Position(0, -1), Position(0, 0), Position(0, 1),
        Position(1, -1), Position(1, 0), Position(1, 1),
    )
    private val imageExtension = 3
    private val offset = Position(imageExtension, imageExtension)

    override fun part1() = matrix.calculateImage(2).countLit()

    override fun part2() = matrix.calculateImage(50).countLit()

    private fun Matrix<String>.calculateImage(cycles: Int): Matrix<String> {
        return (1..cycles).fold(this) { matrix, cycle ->
            val output = matrix.getPixelMasks(cycle).getNewPixels()
            val emptyMatrix = matrix.nextEmptyMatrix()

            emptyMatrix.insertAt(output.associate { (pos, pixel) ->
                (pos + offset) to pixel
            })
        }
    }

    private fun Matrix<String>.nextEmptyMatrix(): Matrix<String> {
        return Matrix(this.numberOfRows + 2 * imageExtension, this.numberOfCols + 2 * imageExtension) { "." }
    }

    private fun Matrix<String>.getPixelMasks(cycle: Int): Map<Position, String> {

        val rows = -imageExtension..this.numberOfRows + imageExtension
        val cols = -imageExtension..this.numberOfCols + imageExtension

        val outerPixel = if (cycle % 2 == 0) imageEnhancementAlgorithm.first().toString() else "."

        return buildMap<Position, String> {
            for (r in rows) {
                for (c in cols) {
                    val p = Position(r, c)
                    val s = buildString {
                        mask.forEach {
                            val pp = p + it
                            if (this@getPixelMasks contains (pp)) {
                                append(this@getPixelMasks[pp])
                            } else append(outerPixel)
                        }
                    }
                    put(p, s)
                }
            }
        }
    }

    private fun Map<Position, String>.getNewPixels() = this.map { (p, pixels) ->
        val number = pixels.toBinary().binaryToDecimal()
        p to imageEnhancementAlgorithm[number].toString()
    }

    private fun Matrix<String>.countLit(): Int {
        val pixelBand = imageExtension + imageExtension / 2
        val rowIndices = pixelBand until this.numberOfRows - pixelBand
        val colIndices = pixelBand until this.numberOfCols - pixelBand

        return rowIndices.sumOf { r ->
            colIndices.count { c -> this[Position(r, c)] == "#" }
        }
    }

    private fun String.toBinary() = this.toList().joinToString("") {
        if (it == '#') "1" else "0"
    }
}           