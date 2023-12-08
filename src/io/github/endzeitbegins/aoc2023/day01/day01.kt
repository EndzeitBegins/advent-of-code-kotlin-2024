package io.github.endzeitbegins.aoc2023.day01

import io.github.endzeitbegins.aoc2023.checkSolution
import io.github.endzeitbegins.aoc2023.readInput

fun part1(input: String): Int {
    return input
        .lines()
        .sumOf { line ->
            val l = line.first { it.isDigit() }
            val r = line.last { it.isDigit() }
            "$l$r".toInt()
        }
}

fun part2(input: String): Int {
    val lookupMapping = mapOf(
        "one" to 1,
        "two" to 2,
        "three" to 3,
        "four" to 4,
        "five" to 5,
        "six" to 6,
        "seven" to 7,
        "eight" to 8,
        "nine" to 9,
        "0" to 0,
        "1" to 1,
        "2" to 2,
        "3" to 3,
        "4" to 4,
        "5" to 5,
        "6" to 6,
        "7" to 7,
        "8" to 8,
        "9" to 9,
    )
    val lookupValues = lookupMapping.keys

    val result = input
        .lines()
        .sumOf { line ->
            val firstValueIndex = lookupValues
                .associateWith { line.indexOf(it) }
                .filterValues { index -> index >= 0 }
                .minBy { (_, index) -> index }
                .key
            val firstValue = lookupMapping.getValue(firstValueIndex)

            val lastValueIndex = lookupValues
                .associateWith { line.lastIndexOf(it) }
                .filterValues { index -> index >= 0 }
                .maxBy { (_, index) -> index }
                .key
            val lastValue = lookupMapping.getValue(lastValueIndex)

            "$firstValue$lastValue".toInt()
        }

    return result
}

fun main() {
    val testInput = readInput("day01/test-input.txt")
    val testInput2 = readInput("day01/test-input2.txt")
    val input = readInput("day01/input.txt")

    // part 1
    checkSolution(part1(testInput), 142)
    println(part1(input))

    // part 2
    checkSolution(part2(testInput2), 281)
    println(part2(input))
}
