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

        (snailfishNumbers[0] + snailfishNumbers[1]).print()
        
        Number.from("[[[[[9,8],1],2],3],4]").depth.print()
        
        snailfishNumbers.first().print().depth.print()
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
    }

    data class Value(val value: Int) : SnailFishNumber() {
        override fun calculateDepth() = 0
    }
}           