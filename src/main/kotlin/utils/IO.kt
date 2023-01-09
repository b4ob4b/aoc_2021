package utils

import utils.IO.createNewDay
import java.io.File
import kotlin.io.path.Path
import kotlin.io.path.absolute


fun main() {
    (1..25).forEach { day -> createNewDay(day, true) }
}

object IO {

    private val basePath = Path("").absolute()
    private val resourcesPath = "$basePath/src/main/resources"
    private val sourceCodePath = "$basePath/src/main/kotlin"

    enum class TYPE(val path: String) {
        SAMPLE("sample"), INPUT("input"), SAMPLE2("sample2")
    }

    fun readFile(day: Int, type: TYPE = TYPE.INPUT): String {
        val filePath = "$resourcesPath/$day/${type.path}.txt"
        return File(filePath).readText()
    }

    fun createNewDay(day: Int, createInputFiles: Boolean = true) {
        if (createInputFiles) createInputFiles(day)
        createKtFile(day, createInputFiles)
    }

    private fun createInputFiles(day: Int) {
        val dir = File("$resourcesPath/$day")
        if (dir.mkdir()) {
            if (File("$dir/sample.txt").createNewFile()) println("sample created")
            if (File("$dir/input.txt").createNewFile()) println("input created")
        }
    }

    private fun createKtFile(day: Int, createInputFiles: Boolean) {
        val formattedDay = day.toString().padStart(2, '0')
        val text = """
            import utils.Day
            import utils.IO
            
            fun main() {
                Day$formattedDay(IO.TYPE.SAMPLE).test()
                Day$formattedDay().solve()
            }
            
            class Day$formattedDay(inputType: IO.TYPE = IO.TYPE.INPUT) : Day("", inputType = inputType) {
                override fun part1(): Any? {
                    return "not yet implement"
                }

                override fun part2(): Any? {
                    return "not yet implement"
                }
            }           
        """.trimIndent()

        val dir = File("$sourceCodePath")
        if (dir.isDirectory) {
            val file = File("$dir/Day$formattedDay.kt")
            if (!file.exists()) {
                file.writeText(text)
                println("Day$formattedDay.kt created")
            } else {
                println("Day$formattedDay.kt already exists")
            }
        }
    }
}
