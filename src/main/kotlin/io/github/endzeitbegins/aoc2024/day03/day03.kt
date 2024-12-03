package io.github.endzeitbegins.aoc2024.day03

import io.github.endzeitbegins.aoc2024.checkSolution
import io.github.endzeitbegins.aoc2024.readInput

private fun part1(input: String): Int =
    findMulInstructions(input)
        .sumOf(MulInstruction::execute)

private fun part2(input: String): Int {
    val disabledRanges = determineDisabledRanges(input)

    return findMulInstructions(input)
        .filterNot { instruction -> instruction.isDisabled(disabledRanges) }
        .sumOf(MulInstruction::execute)
}

private fun findMulInstructions(input: String): Sequence<MulInstruction> {
    val pattern = """mul\((\d{1,3}),(\d{1,3})\)""".toRegex()

    return pattern.findAll(input).map { match ->
        MulInstruction(
            lhs = match.groupValues[1].toInt(),
            rhs = match.groupValues[2].toInt(),
            position = match.range
        )
    }
}

private data class MulInstruction(
    val lhs: Int,
    val rhs: Int,
    val position: IntRange
)

private fun MulInstruction.execute(): Int = lhs * rhs

private fun MulInstruction.isDisabled(disabledRanges: List<IntRange>) =
    disabledRanges.any { disabledRange -> position.first in disabledRange }

private fun determineDisabledRanges(input: String): List<IntRange> {
    val disabledRanges = mutableListOf<IntRange>()

    var searchIndex = 0
    while (searchIndex < input.lastIndex) {
        val disabledStart = input.indexOf("don't()", startIndex = searchIndex)

        if (disabledStart == -1)
            break

        val disabledEnd = input.indexOf("do()", startIndex = disabledStart)
            .takeUnless { it == -1 } ?: input.lastIndex
        searchIndex = disabledEnd + 1

        disabledRanges += disabledStart..disabledEnd
    }

    return disabledRanges
}

fun main() {
    val testInput = readInput("day03/test-input.txt")
    val testInput2 = readInput("day03/test-input2.txt")
    val input = readInput("day03/input.txt")

    // part 1
    checkSolution(part1(testInput), 161)
    println(part1(input))

    // part 2
    checkSolution(part2(testInput2), 48)
    println(part2(input))
}
