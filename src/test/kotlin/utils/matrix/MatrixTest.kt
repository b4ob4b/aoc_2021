package utils.matrix

import io.kotest.matchers.collections.shouldContainExactlyInAnyOrder
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test
import utils.toGrid
import utils.toMatrix

internal class MatrixTest {

    private val matrix = Matrix(
        listOf(
            listOf(1, 2),
            listOf(3, 4)
        )
    )

    @Test
    fun flipHorizontal() {
        matrix.flipHorizontal() shouldBe Matrix(
            listOf(
                listOf(3, 4),
                listOf(1, 2),
            )
        )
    }

    @Test
    fun flipVertical() {
        matrix.flipVertical() shouldBe Matrix(
            listOf(
                listOf(2, 1),
                listOf(4, 3),
            )
        )
    }

    @Test
    fun transpose() {
        matrix.transpose() shouldBe Matrix(
            listOf(
                listOf(1, 3),
                listOf(2, 4)
            )
        )

        """
            123
            456
        """.trimIndent().toGrid().toMatrix().transpose() shouldBe
                """
                    14
                    25
                    36
                """.trimIndent().toGrid().toMatrix()
    }

    @Test
    fun rotateClockWise() {
        matrix.rotateClockWise() shouldBe Matrix(
            listOf(
                listOf(3, 1),
                listOf(4, 2)
            )
        )
    }

    @Test
    fun rotateCounterClockWise() {
        matrix.rotateCounterClockWise() shouldBe Matrix(
            listOf(
                listOf(2, 4),
                listOf(1, 3)
            )
        )
    }

    @Test
    fun search() {
        matrix.search(2).first() shouldBe Position(0, 1)

        Matrix(
            listOf(
                listOf(1, 1),
                listOf(1, 1)
            )
        ).search(1).toList() shouldContainExactlyInAnyOrder listOf(
            Position(0, 0), Position(1, 0), Position(0, 1), Position(1, 1)
        )
    }

    @Test
    fun `second constructor`() {
        Matrix(2, 3) { 1 } shouldBe Matrix(
            listOf(
                listOf(1, 1, 1),
                listOf(1, 1, 1)
            )
        )
    }

    @Test
    fun insertAt() {
        matrix.insertAt(Position(0,0), 0) shouldBe Matrix(
            listOf(
                listOf(0, 2),
                listOf(3, 4)
            )
        )
        
        matrix.insertAt(mapOf(Position(0,0) to 0, Position(1,1) to 0)) shouldBe Matrix(
            listOf(
                listOf(0, 2),
                listOf(3, 0)
            )
        )
    }
}