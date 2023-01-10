import utils.*

fun main() {
    Day16().solve()
}

class Day16(inputType: IO.TYPE = IO.TYPE.INPUT) : Day("Packet Decoder", inputType = inputType) {

    override fun part1(): Int {
        val transmission = Transmission(Hex(input).toBinary())
        return Packet.of(transmission).getSumOfVersions()
    }

    override fun part2(): Long {
        val transmission = Transmission(Hex(input).toBinary())
        return Packet.of(transmission).evaluateExpression()
    }

    sealed class Packet(transmission: Transmission) {
        val version = transmission.take(3).binaryToDecimal()
        val typeId = transmission.take(3).binaryToDecimal()
        val type = PacketType.of(typeId)

        abstract fun getSumOfVersions(): Int

        abstract fun evaluateExpression(): Long

        companion object {
            fun of(transmission: Transmission): Packet {
                return when (transmission.peakPacketType()) {
                    PacketType.Literal -> LiteralPacket(transmission)
                    PacketType.Operator -> OperatorPacket.of(transmission)
                }
            }
        }
    }

    class LiteralPacket(private val transmission: Transmission) : Packet(transmission) {
        private val number = parseNumber()

        private fun parseNumber(): Long {
            val packetEnd = "0"
            var packetEndReached = false

            return buildString {
                while (!packetEndReached) {
                    packetEndReached = transmission.take(1) == packetEnd
                    append(transmission.take(4))
                }
            }.toLong(2)
        }

        override fun getSumOfVersions() = version
        override fun evaluateExpression() = number
    }

    sealed class OperatorPacket(transmission: Transmission) : Packet(transmission) {
        val lengthType = LengthType.of(transmission.take(1))
        var packets: List<Packet>? = null

        abstract fun parsePackets()

        override fun getSumOfVersions(): Int {
            return (packets ?: throw Exception("run parsePackets in initialization"))
                .sumOf { it.getSumOfVersions() } + version
        }

        override fun evaluateExpression(): Long {
            val numbers = (packets ?: throw Exception("run parsePackets in initialization")).map {
                it.evaluateExpression()
            }
            return when (typeId) {
                0 -> numbers.sum()
                1 -> numbers.product()
                2 -> numbers.min()
                3 -> numbers.max()
                5 -> if (numbers[0] > numbers[1]) 1 else 0
                6 -> if (numbers[0] < numbers[1]) 1 else 0
                7 -> if (numbers[0] == numbers[1]) 1 else 0
                
                else -> throw Exception("typeId: $typeId not defined")
            }
        }

        companion object {
            fun of(transmission: Transmission) = when (transmission.peakLengthType()) {
                LengthType.Bit11 -> Operator11BitPacket(transmission)
                LengthType.Bit15 -> Operator15BitPacket(transmission)
            }
        }

    }

    class Operator11BitPacket(private val transmission: Transmission) : OperatorPacket(transmission) {
        private val numberOfPackets = transmission.take(lengthType.length).binaryToDecimal()

        init {
            parsePackets()
        }

        override fun parsePackets() {
            packets = buildList {
                repeat(numberOfPackets) {
                    add(Packet.of(transmission))
                }
            }
        }
    }

    class Operator15BitPacket(private val transmission: Transmission) : OperatorPacket(transmission) {
        private val packetsLength = transmission.take(lengthType.length).binaryToDecimal()

        init {
            parsePackets()
        }

        override fun parsePackets() {
            val currentLength = transmission.length

            packets = buildList {
                while (transmission.length > (currentLength - packetsLength)) {
                    add(Packet.of(transmission))
                }
            }
        }
    }

    class Hex(private val string: String) {
        private val hexBinMap = mapOf(
            '0' to "0000",
            '1' to "0001",
            '2' to "0010",
            '3' to "0011",
            '4' to "0100",
            '5' to "0101",
            '6' to "0110",
            '7' to "0111",
            '8' to "1000",
            '9' to "1001",
            'A' to "1010",
            'B' to "1011",
            'C' to "1100",
            'D' to "1101",
            'E' to "1110",
            'F' to "1111",
        )

        fun toBinary() = Binary(string.toList().joinToString("") { hexBinMap[it] ?: throw Exception("unknown hex char: $it") })
    }

    data class Binary(val binary: String)

    class Transmission(binary: Binary) {
        private var stream = binary.binary
        val length: Int
            get() = stream.length

        fun take(n: Int): String {
            val string = stream.take(n)
            stream = stream.removeRange(0 until n)
            return string
        }

        fun peakPacketType() = PacketType.of(stream.slice(3..5).binaryToDecimal())

        fun peakLengthType() = LengthType.of(stream[6].toString())
    }

    enum class PacketType {
        Literal, Operator;

        companion object {
            fun of(int: Int) = when (int) {
                4 -> Literal
                else -> Operator
            }
        }
    }

    enum class LengthType(val length: Int) {
        Bit11(11), Bit15(15);

        companion object {
            fun of(string: String) = when (string) {
                "0" -> Bit15
                "1" -> Bit11
                else -> throw Exception("unknown: $string")
            }
        }
    }

}           