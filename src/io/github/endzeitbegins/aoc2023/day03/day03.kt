package io.github.endzeitbegins.aoc2023.day03

import io.github.endzeitbegins.aoc2023.checkSolution
import io.github.endzeitbegins.aoc2023.readInput

private val nonSymbols = ('0'..'9').toSet() + '.'

data class SchematicNumber(
    val value: Int,
    val row: Int,
    val columns: IntRange,
)

data class Gear(
    val lhs: SchematicNumber,
    val rhs: SchematicNumber,
) {
    val gearRatio: Long = 1L * lhs.value * rhs.value
}

data class Schematic(private val rawSchematic: String) {

    private val lines = rawSchematic.lines()
    private val height = lines.size
    private val width = lines.first().length

    private val schematicNumbers: List<SchematicNumber> = determineSchematicNumbers()

    val partNumbers = schematicNumbers.filter { it.isPartNumber() }
    val gears: List<Gear> = determineGears()

    operator fun get(x: Int, y: Int): Char
            = lines[y][x]

    private fun getOrNull(x: Int, y: Int): Char? {
        if (x < 0 || x >= width) return null
        if (y < 0 || y >= height) return null
        return this[x, y]
    }

    private fun Char.isSymbol(): Boolean {
        return this !in nonSymbols
    }

    private fun determineSchematicNumbers(): List<SchematicNumber> {
        val numbers = mutableListOf<SchematicNumber>()
        val digits = StringBuilder()

        for (y in 0 until height) {
            for (x in 0 until width) {
                val char = this[x, y]

                if (char.isDigit()) {
                    digits.append(char)
                } else if (digits.isNotEmpty()) {
                    val value = digits.toString().toInt()

                    numbers.add(SchematicNumber(
                        value = value,
                        row = y,
                        columns = (x - digits.length) until x
                    ))
                    digits.clear()
                }
            }
            if (digits.isNotEmpty()) {
                val value = digits.toString().toInt()

                numbers.add(SchematicNumber(
                    value = value,
                    row = y,
                    columns = (width - digits.length) until width
                ))
                digits.clear()
            }
        }

        return numbers
    }

    private fun SchematicNumber.determineSurroundingPositions(): Sequence<Pair<Int, Int>> {
        return sequence {
            val startX = columns.first() - 1
            val endX = columns.last() + 1

            for(x in startX..endX) {
                yield(x to row - 1)
            }
            yield(startX to row)
            yield(endX to row)
            for(x in startX..endX) {
                yield(x to row + 1)
            }
        }
    }

    private fun SchematicNumber.isPartNumber(): Boolean {
        return determineSurroundingPositions()
            .map { (x, y) -> getOrNull(x, y) }
            .filterNotNull()
            .any { char -> char.isSymbol() }
    }

    private fun determineGears(): List<Gear> {
        val gears = mutableListOf<Gear>()

        for (y in 0 until height) {
            for (x in 0 until width) {
                val char = this[x, y]

                if (char != '*') {
                    continue
                }

                val xRange = x-1..x+1
                val yRange = y-1..y+1

                val parts = partNumbers.filter { pn ->
                    pn.row in yRange && pn.columns.any { it in xRange }
                }

                if (parts.size == 2) {
                    gears.add(
                        Gear(parts[0], parts[1])
                    )
                }
            }
        }

        return gears
    }
}

fun part1(input: String): Int {
    val schematic = Schematic(input)

    return schematic.partNumbers.sumOf { it.value }
}

fun part2(input: String): Long {
    val schematic = Schematic(input)

    return schematic.gears.sumOf { it.gearRatio }
}

fun main() {
    val testInput = readInput("day03/test-input.txt")
    val input = readInput("day03/input.txt")

    // part 1
    checkSolution(part1(testInput), 4361)
    println(part1(input))

    // part 2
     checkSolution(part2(testInput), 467835)
     println(part2(input))
}
