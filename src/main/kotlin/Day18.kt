import utils.Day
import utils.IO
import utils.print
import utils.splitLines

fun main() {
    Day18(IO.TYPE.SAMPLE).test(4140)
    Day18().solve()
}

class Day18(inputType: IO.TYPE = IO.TYPE.INPUT) : Day("", inputType = inputType) {

    override fun part1(): Int {
        val numbers = input.splitLines()

        val snailfishNumbers = numbers.map { SnailFishNumber.from(it) }
        return snailfishNumbers.reduce { acc, number -> (acc + number).reduce() }.calculateMagnitude()
    }

    override fun part2(): Any? {
        return "not yet implement"
    }

    sealed class SnailFishNumber {
        val depth: Int
            get() {
                return calculateDepth()
            }

        abstract fun calculateMagnitude(): Int

        abstract fun calculateDepth(): Int

        fun reduce(): SnailFishNumber {
            var number = this
            var explosion = number.explode()
            var split = explosion.split()
            
            while (split != number) {
                number = split
                explosion = number.explode()
                split = explosion.split()
            }
            return number
        }

        fun explode(): SnailFishNumber {
            var number = this
            var explosion = explode(0).snailFishNumber
            while (number != explosion) {
                number = explosion
                explosion = explosion.explode(0).snailFishNumber
            }
            return number
        }

        abstract fun explode(depth: Int, found: Boolean = false): Explosion

        operator fun plus(other: SnailFishNumber): SnailFishNumber {
            return Number(this to other)
        }

        abstract fun split(): SnailFishNumber

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

    data class Explosion(val left: Int? = null, val snailFishNumber: SnailFishNumber, val right: Int? = null, val found: Boolean = false)

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

        override fun calculateMagnitude(): Int {
            val (left, right) = pair
            return left.calculateMagnitude() * 3 + right.calculateMagnitude() * 2
        }

        override fun calculateDepth(): Int {
            return pair.toList().maxOf { it.depth } + 1
        }

        override fun split(): SnailFishNumber {
            var (left, right) = pair
            val splitLeft = left.split()
            if (splitLeft == left) {
                right = right.split()
            }
            return Number(splitLeft to right)
        }

        override fun explode(depth: Int, found: Boolean): Explosion {
            var (left, right) = pair
            var explosionFound = found
            if (depth == 4) {
                return Explosion((left as Value).value, Value(0), (right as Value).value, true)
            }

            var leftUp: Int? = null
            var rightUp: Int? = null

            if (left is Number) {
                val (newLeft, middle, newRight, found) = left.explode(depth + 1)
                explosionFound = found
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
            if (right is Number && !explosionFound) {
                val (newLeft, middle, newRight, found) = right.explode(depth + 1)
                explosionFound = found
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



            return Explosion(leftUp, Number(left to right), rightUp, explosionFound)
        }

        private fun addRight(newLeft: Int): SnailFishNumber {
            val (left, right) = pair
            return when (right) {
                is Value -> Number(left to Value(right.value + newLeft))
                is Number -> Number(left to right.addRight(newLeft))
            }
        }

        private fun addLeft(newRight: Int): SnailFishNumber {
            val (left, right) = pair
            return when (left) {
                is Value -> Number(Value(left.value + newRight) to right)
                is Number -> Number(left.addLeft(newRight) to right)
            }
        }

        override fun toString(): String {
            val (left, right) = pair
            return "[$left,$right]"
        }
    }

    data class Value(val value: Int) : SnailFishNumber() {
        override fun calculateMagnitude(): Int {
            return value
        }

        override fun calculateDepth() = 0
        override fun explode(depth: Int, found: Boolean): Explosion {
            return Explosion(snailFishNumber = this)
        }

        override fun split(): SnailFishNumber {
            return if (value > 9) {
                if (value % 2 == 0) {
                    Number(Value(value / 2) to Value(value / 2))
                } else {
                    Number(Value(value / 2) to Value(value / 2 + 1))
                }
            } else {
                this
            }
        }

        override fun toString(): String {
            return "$value"
        }
    }
}           