package io.github.endzeitbegins.aoc2023.day15

import io.github.endzeitbegins.aoc2023.checkSolution
import io.github.endzeitbegins.aoc2023.readInput

fun String.calculateHash(): Int {
    var hashValue = 0

    for (char in this) {
        val asciiValue = char.code

        hashValue += asciiValue
        hashValue *= 17
        hashValue %= 256
    }

    return hashValue
}

fun part1(input: String): Int {
    val steps = input.split(',')

    return steps
        .sumOf { step -> step.calculateHash() }
}

fun part2(input: String): Int {
    return input.length
}

fun main() {
    val testInput = readInput("day15/test-input.txt")
    val testInput2 = readInput("day15/test-input2.txt")
    val input = readInput("day15/input.txt")

    // part 1
    checkSolution(part1(testInput), 52)
    checkSolution(part1(testInput2), 1320)
    println(part1(input))

    // part 2
    // checkSolution(part2(testInput), 1)
    // println(part2(input))
}
