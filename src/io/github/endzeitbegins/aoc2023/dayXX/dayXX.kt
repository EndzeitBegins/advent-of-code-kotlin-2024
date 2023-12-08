package io.github.endzeitbegins.aoc2023.dayXX

import io.github.endzeitbegins.aoc2023.checkSolution
import io.github.endzeitbegins.aoc2023.readInput

fun part1(input: String): Int {
    return input.length
}

fun part2(input: String): Int {
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
