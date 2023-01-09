package utils.navigation

import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test
import utils.print

internal class Position3DTest {

    @Test
    fun plus() {
        Position3D(4, 5, 6) + Position3D(1, 2, 3) shouldBe Position3D(5, 7, 9)
        Position3D(-1, -2, -3) + Position3D(1, 2, 3) shouldBe Position3D.origin
    }

    @Test
    fun manhattenDistance() {
        Position3D(3, 4, 5).manhattenDistance shouldBe 12
        Position3D(-3, -4, 5).manhattenDistance shouldBe 12
    }

    @Test
    fun getNeighbours() {
        Position3D.origin.getNeighbours().toList().size shouldBe 9 + 8 + 9
        Position3D.origin.getNeighbours().toList().print()
    }

    @Test
    fun times() {
        Position3D(1, 2, 3) * 3 shouldBe Position3D(3, 6, 9)
    }

    @Test
    fun getPathThrough() {
        Position3D.origin.getPathThrough(Position3D(1,1,1)).take(3).last() shouldBe Position3D(3,3,3)
        Position3D.origin.getPathThrough(Position3D(1,-1,2)).take(3).last() shouldBe Position3D(3,-3,6)
    }

    @Test
    fun minus() {
        Position3D.origin - Position3D(1,1,2) shouldBe Position3D(-1,-1,-2)
    }
}