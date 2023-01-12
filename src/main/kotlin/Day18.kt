import utils.Day
import utils.IO
import utils.print
import utils.splitLines

fun main() {
    Day18(IO.TYPE.SAMPLE).test(4140, 3993)
    Day18().solve()
}

class Day18(inputType: IO.TYPE = IO.TYPE.INPUT) : Day("", inputType = inputType) {

    private val numbers = input.splitLines()
    private val snailfishNumbers = numbers.map { SnailFishNumber.from(it) }

    override fun part1(): Int {
        return snailfishNumbers.reduce { acc, number -> (acc + number).reduce() }.calculateMagnitude()
    }

    override fun part2(): Any? {
        return snailfishNumbers.flatMap { i ->
            snailfishNumbers.mapNotNull { j ->
                if (i != j) i + j else null
            }.map { it.reduce().calculateMagnitude() }
        }.max()
    }

    sealed class SnailFishNumber {
        abstract fun calculateMagnitude(): Int

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
            return Number(this, other)
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

    data class Number(val left: SnailFishNumber, val right: SnailFishNumber) : SnailFishNumber() {
        companion object {
            fun from(string: String): Number {
                var current = ""
                var brackets = 0
                var left: SnailFishNumber? = null
                for (char in string.drop(1).dropLast(1)) {
                    if (char == '[') brackets++
                    if (char == ']') brackets--
                    if (char == ',' && brackets == 0) {
                        left = SnailFishNumber.from(current)
                        current = ""
                        continue
                    }
                    current += char
                }
                val right = SnailFishNumber.from(current)

                if (left == null) throw Exception("unexpected input")
                return Number(left, right)
            }
        }

        override fun calculateMagnitude(): Int {
            return left.calculateMagnitude() * 3 + right.calculateMagnitude() * 2
        }

        override fun split(): SnailFishNumber {
            var right = right
            val splitLeft = left.split()
            if (splitLeft == left) {
                right = right.split()
            }
            return Number(splitLeft, right)
        }


        override fun explode(depth: Int, found: Boolean): Explosion {
            var left = left
            var right = right
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

            return Explosion(leftUp, Number(left, right), rightUp, explosionFound)
        }

        private fun addRight(newLeft: Int): SnailFishNumber {
            return when (right) {
                is Value -> Number(left, Value(right.value + newLeft))
                is Number -> Number(left, right.addRight(newLeft))
            }
        }

        private fun addLeft(newRight: Int): SnailFishNumber {
            return when (left) {
                is Value -> Number(Value(left.value + newRight), right)
                is Number -> Number(left.addLeft(newRight), right)
            }
        }

        override fun toString(): String {
            return "[$left,$right]"
        }
    }

    data class Value(val value: Int) : SnailFishNumber() {
        override fun calculateMagnitude(): Int {
            return value
        }

        override fun explode(depth: Int, found: Boolean): Explosion {
            return Explosion(value, Value(0), value, true)
        }

        override fun split(): SnailFishNumber {
            val half = value / 2
            val isEven = value % 2 == 0
            return if (value > 9) {
                val left = Value(half)
                val right = Value(if (isEven) half else half + 1)
                Number(left, right)
            } else {
                this
            }
        }

        override fun toString(): String {
            return "$value"
        }
    }
}           