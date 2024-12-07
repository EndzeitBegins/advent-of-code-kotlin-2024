package io.github.endzeitbegins.aoc2024.day07

import io.github.endzeitbegins.aoc2024.checkSolution
import io.github.endzeitbegins.aoc2024.readInput

private data class PartialEquation(
    val result: Long,
    val values: List<Long>,
)

private fun part1(input: String): Long =
    parsePartialEquations(input)
        .filter { partialEquation -> partialEquation.isSolvableWith(setOf(Long::plus, Long::times)) }
        .sumOf(PartialEquation::result)

private fun part2(input: String): Long  =
    parsePartialEquations(input)
        .filter { partialEquation -> partialEquation.isSolvableWith(setOf(Long::plus, Long::times, Long::concat)) }
        .sumOf(PartialEquation::result)

private fun parsePartialEquations(input: String) = input
    .lineSequence()
    .map { line ->
        val (result, valuePart) = line.split(": ", limit = 2)
        val values = valuePart.split(" ")

        PartialEquation(
            result = result.toLong(),
            values = values.map(String::toLong)
        )
    }

private fun PartialEquation.isSolvableWith(operators: Set<Long.(Long) -> Long>): Boolean {
    var results = listOf(values.first())

    for (value in values.subList(1, values.size)) {
        results = results.flatMap { partialResult ->
            operators.map { operator -> partialResult.operator(value) }
        }
    }

    return result in results
}

private fun Long.concat(other: Long): Long =
    "$this$other".toLong()

fun main() {
    val testInput = readInput("day07/test-input.txt")
    val input = readInput("day07/input.txt")

    // part 1
    checkSolution(part1(testInput), 3749)
    println(part1(input))

    // part 2
    checkSolution(part2(testInput), 11387)
    println(part2(input))
}
