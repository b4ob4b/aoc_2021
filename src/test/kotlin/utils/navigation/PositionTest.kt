package utils.navigation

import io.kotest.matchers.collections.shouldNotContainInOrder
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test
import utils.Position
import utils.getLine

internal class PositionTest {

    @Test
    fun manhattenDistance() {
        Position(1, 2).manhattenDistance shouldBe 3
        Position(1, -2).manhattenDistance shouldBe 3
        Position(-1, -2).manhattenDistance shouldBe 3
        Position(-1, 2).manhattenDistance shouldBe 3
    }

    @Test
    fun doMovement() {
        Position(0, 0).doMovement(Direction4.East) shouldBe Position(1, 0)
        Position(0, 0).doMovement(Direction4.North) shouldBe Position(0, 1)
        Position(0, 0).doMovement(Direction4.West) shouldBe Position(-1, 0)
        Position(0, 0).doMovement(Direction4.South) shouldBe Position(0, -1)
    }

    @Test
    fun get4Neighbours() {
        Position(0, 0).get4Neighbours().toList() shouldNotContainInOrder listOf(
            Position(1, 0),
            Position(0, 1),
            Position(-1, 0),
            Position(0, -1),
        )
    }

    @Test
    fun get8Neighbours() {
        Position(0, 0).get4Neighbours().toList() shouldNotContainInOrder listOf(
            Position(0, 1),
            Position(1, 1),
            Position(1, 0),
            Position(1, -1),
            Position(0, -1),
            Position(-1, -1),
            Position(-1, 0),
            Position(-1, 1),
        )
    }
    
    @Test
    fun `get line between two points`() {
        Pair(Position(0,0), Position(0,4)).getLine() shouldBe listOf(
            Position(0,0),
            Position(0,1),
            Position(0,2),
            Position(0,3),
            Position(0,4),
        )

        Pair(Position(0,0), Position(3,0)).getLine() shouldBe listOf(
            Position(0,0),
            Position(1,0),
            Position(2,0),
            Position(3,0),
        )

        Pair(Position(3,0), Position(0,0)).getLine() shouldBe listOf(
            Position(3,0),
            Position(2,0),
            Position(1,0),
            Position(0,0),
        )
    }
    
    @Test
    fun `get path trough`() {
        Position.origin.getPathThrough(Position(0,1)).take(3).toList() shouldBe listOf(
            Position.origin,
            Position(0,1),
            Position(0,2),
        )
        Position.origin.getPathThrough(Position(-1,0)).take(3).toList() shouldBe listOf(
            Position.origin,
            Position(-1,0),
            Position(-2,0),
        )
        Position.origin.getPathThrough(Position(1,1)).take(3).toList() shouldBe listOf(
            Position.origin,
            Position(1,1),
            Position(2,2),
        )
    }
}