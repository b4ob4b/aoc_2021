package utils

import utils.matrix.Matrix

fun main() {
    listOf(1,2,3,1,2,1).allIndicesOf(1).toList().print()
    // [0, 3, 5]
    
    """
        123
        456
    """.trimIndent().toGrid().valueOf(Position(2,1)).print()
    // 6
    
}

fun <T> Iterable<T>.allIndicesOf(element: T) = sequence {
    var index = 0
    this@allIndicesOf.iterator().forEach {
        if(it == element) yield(index)
        index++
    }
}

fun Collection<Int>.product() = this.reduce { acc, i -> acc * i }
fun Collection<Long>.product() = this.reduce { acc, i -> acc * i }

fun <T> List<List<T>>.valueOf(position: Position) = this[position.x][position.y]

fun <T> List<List<T>>.toMatrix() = Matrix(this)

fun <T> List<List<T>>.toField() = Field(this.reversed())