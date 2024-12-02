package io.github.endzeitbegins.aoc2024.day02

import io.github.endzeitbegins.aoc2024.checkSolution
import io.github.endzeitbegins.aoc2024.readInputLines

private fun part1(input: List<String>): Int =
    parseReports(input)
        .count { report -> report.isSafe() }

private fun List<Int>.isSafe(): Boolean {
    val allowedRange = if (this[0] < this[1]) -3..-1 else 1..3

    return windowed(size = 2, step = 1)
        .all { (left, right) -> left - right in allowedRange }
}

private fun part2(input: List<String>): Int =
    parseReports(input)
        .count { report -> report.isSafeWithTolerance() }

private fun parseReports(input: List<String>) =
    input.map { line -> line.split(" ").map(String::toInt) }

private fun List<Int>.isSafeWithTolerance(): Boolean {
    val allowedRange = if (this[0] < this[1]) -3..-1 else 1..3

    for (index in 0 until lastIndex) {
        val isSaveLevel = this[index] - this[index + 1] in allowedRange

        if (isSaveLevel) {
            continue
        }

        return this.withoutItemAtIndexOrNull(index - 1)?.isSafe() ?: false
                || this.withoutItemAtIndexOrNull(index)?.isSafe() ?: false
                || this.withoutItemAtIndexOrNull(index + 1)?.isSafe() ?: false
    }

    return true
}

private fun <E> List<E>.withoutItemAtIndexOrNull(index: Int): List<E>? {
    if (index < 0 || index > lastIndex)
        return null

    val mutableCopy = this.toMutableList()
    mutableCopy.removeAt(index)

    return mutableCopy
}

fun main() {
    val testInput = readInputLines("day02/test-input.txt")
    val input = readInputLines("day02/input.txt")

    // part 1
    checkSolution(part1(testInput), 2)
    println(part1(input))

    // part 2
    checkSolution(part2(testInput), 4)
    println(part2(input))
}
