import utils.*

fun main() {
    Day21(IO.TYPE.SAMPLE).test(739785)
    Day21().solve()
}

class Day21(inputType: IO.TYPE = IO.TYPE.INPUT) : Day("Dirac Dice", inputType = inputType) {

    private val startingPosition = input.splitLines().map { it.last().digitToInt() }.toPair()
    private val spaces = generateSequence(1) {
        val next = it + 1
        if (next == 11) 1 else next
    }
    private val dice = Dice()
    


    override fun part1(): Any? {
        var p1 = startingPosition.first
        var p2 = startingPosition.second

        var s1 = 0
        var s2 = 0

        while (s2 < 1000) {
            val d1 = dice.roll()
            p1 = spaces.take(p1 + d1.sum()).last()
            s1 += p1
            "${dice.rolls}: Player 1 rolls $d1 and moves to space $p1 for a total score of $s1".print()
            if (s1 >= 1000) break

            val d2 = dice.roll()
            p2 = spaces.take(p2 + d2.sum()).last()
            s2 += p2
            "${dice.rolls}: Player 2 rolls $d2 and moves to space $p2 for a total score of $s2".print()
        }

        val scoreLosing = if (s1 < s2) s1 else s2
        return dice.rolls * scoreLosing
    }

    override fun part2(): Any? {
        return "not yet implement"
    }

    class Dice {

        var rolls = 0
        var current = 0
        val maxFaces = 100

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