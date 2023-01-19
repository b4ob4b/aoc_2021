import utils.*

fun main() {
    Day21(IO.TYPE.SAMPLE).test(739785, 444356092776315L)
    Day21().solve()
}

class Day21(inputType: IO.TYPE = IO.TYPE.INPUT) : Day("Dirac Dice", inputType = inputType) {

    private val startingPosition = input.splitLines().map { it.last().digitToInt() }.toPair()
    private val spaces = generateSequence(1) {
        val next = it + 1
        if (next == 11) 1 else next
    }
    private val player1 = Player(startingPosition.first, 0)
    private val player2 = Player(startingPosition.second, 0)
    private val dice = Dice()
    private val memo: MutableMap<Pair<Player, Player>, Pair<Long, Long>> = mutableMapOf()

    override fun part1(): Int {
        val score = player1 playDiracDice player2
        return score.toList().product()
    }

    override fun part2(): Long {
        val wins = player1 playQuantumDice player2
        return wins.toList().max()
    }

    private infix fun Player.playDiracDice(other: Player): Pair<Int, Int> {
        val diceNumbers = dice.roll()
        val newSpace = spaces.take(space + diceNumbers.sum()).last()
        val newScore = score + newSpace

        if (newScore >= 1000) return other.score to dice.rolls

        return other playDiracDice Player(newSpace, newScore)
    }

    private infix fun Player.playQuantumDice(other: Player): Pair<Long, Long> {
        if (this.score >= 21) return 1L to 0L
        if (other.score >= 21) return 0L to 1L

        if ((this to other) in memo) return memo[this to other]!!

        var wins1 = 0L
        var wins2 = 0L

        (1..3).forEach { d1 ->
            (1..3).forEach { d2 ->
                (1..3).forEach { d3 ->
                    val newSpace = spaces.take(this.space + d1 + d2 + d3).last()
                    val newScore = newSpace + this.score

                    val (w2, w1) = other playQuantumDice Player(newSpace, newScore)
                    wins1 += w1
                    wins2 += w2
                }
            }
        }
        memo[this to other] = wins1 to wins2
        return wins1 to wins2
    }

    data class Player(val space: Int, val score: Int)

    class Dice {
        var rolls = 0
        var current = 0
        private val maxFaces = 100

        fun roll(): List<Int> {
            rolls += 3
            val faces = (1..3)
                .map { it + current }
                .map {
                    if (it > maxFaces) it % 100 else it
                }
            current = faces.last()
            return faces
        }
    }
}           