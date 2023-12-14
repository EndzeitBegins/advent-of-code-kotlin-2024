package io.github.endzeitbegins.aoc2023.day12

import io.github.endzeitbegins.aoc2023.checkSolution
import io.github.endzeitbegins.aoc2023.readInput
import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext


data class ConditionRecord(
    val row: String,
    val damagedSprings: List<Int>,
)

private val String.damagedSpringsNotation: List<Int>
    get() = this
        .split("""[.]+""".toRegex())
        .filterNot(String::isBlank)
        .map(String::length)

private fun List<Int>.isCompatibleWith(expectedDamagedSpringsNotation: List<Int>): Boolean {
    if (this.size > expectedDamagedSpringsNotation.size) {
        return false
    }

    for (index in this.indices) {
        val actual = this[index]
        val expected = expectedDamagedSpringsNotation[index]

        if (actual > expected) {
            return false
        }

        if (index < this.lastIndex && actual < expected) {
            return false
        }
    }

    return true
}

fun parseConditionRecords(input: String): List<ConditionRecord> {
    return input
        .lines()
        .map { line ->
            val (row, rawDamagedSprings) = line.split(" ", limit = 2)
            val damagedSprings = rawDamagedSprings.split(",").map(String::toInt)

            ConditionRecord(row = row, damagedSprings = damagedSprings)
        }
}

private fun ConditionRecord.countPossiblePermutations(): Int =
    determinePossiblePermutations().size

private fun ConditionRecord.determinePossiblePermutations(
    partialRow: String = ""
): List<String> {
    val startIndex = partialRow.length
    val indexOfNextUnknown = row.indexOf('?', startIndex = startIndex)
    val updatedRow = if (indexOfNextUnknown > startIndex) {
        partialRow + row.substring(startIndex, indexOfNextUnknown)
    } else if (indexOfNextUnknown < 0) {
        partialRow + row.substring(startIndex)
    } else partialRow

    return if (updatedRow.damagedSpringsNotation.isCompatibleWith(expectedDamagedSpringsNotation = damagedSprings)) {
        if (indexOfNextUnknown < 0) {
            if (updatedRow.damagedSpringsNotation != damagedSprings) {
                // there is no unknown place left; but the pattern does not match
                emptyList()
            } else {
                // there is no unknown place left; the pattern matches, we are done
                listOf(updatedRow)
            }
        } else {
            listOf(
                determinePossiblePermutations("$updatedRow."),
                determinePossiblePermutations("$updatedRow#"),
            ).flatten()
        }
    } else {
        // calculated permutation is not compatible anymore, abort this path
        emptyList()
    }
}

private fun ConditionRecord.unfold(): ConditionRecord {
    val unfoldedRow = buildString {
        repeat(5) { index ->
            if (index > 0) {
                append('?')
            }
            append(row)
        }
    }

    val unfoldedDamagedSprings = buildList {
        repeat(5) {
            addAll(damagedSprings)
        }
    }

    return ConditionRecord(unfoldedRow, unfoldedDamagedSprings)
}

fun part1(input: String): Int {
    return parseConditionRecords(input)
        .sumOf { conditionRecord -> conditionRecord.countPossiblePermutations() }
}

fun part2(input: String): Int {
    return runBlocking {
        val permutations = parseConditionRecords(input)
            .map { record -> record.unfold() }
            .map { conditionRecord ->
                async(Dispatchers.Default) {
                    val countPossiblePermutations = conditionRecord.countPossiblePermutations()
                    println('X')
                    countPossiblePermutations
                }
            }

        permutations.awaitAll().sum()
    }

}

fun main() {
    val testInput = readInput("day12/test-input.txt")
    val input = readInput("day12/input.txt")

    // part 1
    checkSolution(part1(testInput), 21)
    println(part1(input))

    // part 2
     checkSolution(part2(testInput), 525152)
     println(part2(input))
}
