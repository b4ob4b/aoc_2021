package utils

import utils.navigation.Position3D

fun main() {
    """
        1
        2
    """.trimIndent().splitLines().print()
    // [1, 2]

    "10100".binaryToDecimal().print()
    // 20

    "1,2,3,4".extractInts().print()
    // [1, 2, 3, 4]

    """
        123
        456
    """.trimIndent().toGrid().print()
    // [[1, 2, 3], [4, 5, 6]]

    """
        1,2,3
        4,5,6
    """.trimIndent().toGrid(",") { it.toInt() + 1 }.print()
    // [[2, 3, 4], [5, 6, 7]]
}

fun <T> T.print() = this.also { println(it) }

fun <T> Collection<T>.fPrint() = this.forEach { println(it) }

fun String.splitLines() = split("\n")

fun String.splitLinesToInt() = split("\n").map(String::toInt)

fun String.binaryToDecimal() = Integer.parseInt(this, 2)

fun String.extractInts(separator: String = ",") = this.split(separator).map { it.toInt() }

fun String.toGrid(separator: String = "", filterBlanks: Boolean = true) =
    this.splitLines().map { line ->
        line.split(separator).let {
            if (filterBlanks) {
                it.filter { it.isNotBlank() }
            } else {
                it
            }
        }
    }

fun <T> String.toGrid(separator: String = "", mapCell: ((String) -> T)): List<List<T>> =
    this.toGrid(separator).map { it.map(mapCell) }

fun String.toPosition3D() = this.split(",").map { it.toInt() }
    .let { (x, y, z) -> Position3D(x, y, z) }
