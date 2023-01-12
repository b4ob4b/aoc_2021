import utils.Day
import utils.IO
import utils.print
import utils.splitLines

fun main() {
    Day18(IO.TYPE.SAMPLE).test(4140)
    Day18().solve()
}

class Day18(inputType: IO.TYPE = IO.TYPE.INPUT) : Day("", inputType = inputType) {

    override fun part1(): Any? {
        val numbers = input.splitLines()
        val snailfishNumbers = numbers.map { Number.from(it) }

        (Number.from("[[[[[9,8],1],2],3],4]").reduce() == Number.from("[[[[0,9],2],3],4]")).print()
        (Number.from("[7,[6,[5,[4,[3,2]]]]]").reduce() == Number.from("[7,[6,[5,[7,0]]]]")).print()
        (Number.from("[[6,[5,[4,[3,2]]]],1]").reduce() == Number.from("[[6,[5,[7,0]]],3]")).print()
        (Number.from("[[3,[2,[1,[7,3]]]],[6,[5,[4,[3,2]]]]]").reduce() == Number.from("[[3,[2,[8,0]]],[9,[5,[7,0]]]]")).print()
        
        return 1
    }

    override fun part2(): Any? {
        return "not yet implement"
    }

    sealed class SnailFishNumber {
        val depth: Int
            get() {
                return calculateDepth()
            }

        abstract fun calculateDepth(): Int

        operator fun plus(other: SnailFishNumber): SnailFishNumber {
            return Number(this to other)
        }

        companion object {
            fun from(string: String): SnailFishNumber {
                val number = string.toIntOrNull()
                if (number != null) {
                    return Value(number)
                }
                return Number.from(string)
            }
        }
    }

    data class Number(val pair: Pair<SnailFishNumber, SnailFishNumber>) : SnailFishNumber() {
        companion object {
            fun from(string: String): Number {
                var current = ""
                var brackets = 0
                val pair = buildList {
                    for (char in string.drop(1).dropLast(1)) {
                        if (char == '[') brackets++
                        if (char == ']') brackets--
                        if (char == ',' && brackets == 0) {
                            add(SnailFishNumber.from(current))
                            current = ""
                            continue
                        }
                        current += char
                    }
                    add(SnailFishNumber.from(current))
                }.zipWithNext().single()

                return Number(pair)
            }
        }

        override fun calculateDepth(): Int {
            return pair.toList().maxOf { it.depth } + 1
        }

        fun reduce(): SnailFishNumber {
            return explode(0).second
        }

        private fun explode(depth: Int): Triple<Int?, SnailFishNumber, Int?> {
            if (pair == Value(3) to Value(2)) {
                println()
            }
            var (left, right) = pair
            if (depth == 4) {
                return Triple((left as Value).value, Value(0), (right as Value).value)
            }

            var leftUp: Int? = null
            var rightUp: Int? = null

            if (left is Number) {
                val (newLeft, middle, newRight) = left.explode(depth + 1)
                left = middle
                if (newRight != null) {
                    right = when (right) {
                        is Value -> Value(right.value + newRight)
                        is Number -> right.addLeft(newRight)
                    }
                }
                if (newLeft != null) {
                    leftUp = newLeft
                }
            }
            if (right is Number) {
                val (newLeft, middle, newRight) = right.explode(depth + 1)
                right = middle
                if (newLeft != null) {
                    left = when (left) {
                        is Value -> Value(left.value + newLeft)
                        is Number -> left.addRight(newLeft)
                    }
                }
                if (newRight != null) {
                    rightUp = newRight
                }
            }
            println()

            return Triple(leftUp, Number(left to right), rightUp)
        }

        private fun addRight(newLeft: Int): SnailFishNumber {
            val (left, right) = pair
            return when (right) {
                is Value -> Number(left to Value(right.value + newLeft))
                is Number -> right.addRight(newLeft)
            }
        }

        private fun addLeft(newRight: Int): SnailFishNumber {
            val (left, right) = pair
            return when (left) {
                is Value -> Number(Value(left.value + newRight) to right)
                is Number -> left.addLeft(newRight)
            }
        }
    }

    data class Value(val value: Int) : SnailFishNumber() {
        override fun calculateDepth() = 0
    }
}           