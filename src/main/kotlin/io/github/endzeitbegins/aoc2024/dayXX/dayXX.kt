package io.github.endzeitbegins.aoc2024.dayXX

import io.github.endzeitbegins.aoc2024.checkSolution
import io.github.endzeitbegins.aoc2024.readInput

private fun part1(input: String): Int {
    return input.length
}

private fun part2(input: String): Int {
    return input.length
}

fun main() {
    val testInput = readInput("dayXX/test-input.txt")
    val input = readInput("dayXX/input.txt")

    // part 1
    checkSolution(part1(testInput), 1)
    println(part1(input))

    // part 2
    // checkSolution(part2(testInput), 1)
    // println(part2(input))
}
