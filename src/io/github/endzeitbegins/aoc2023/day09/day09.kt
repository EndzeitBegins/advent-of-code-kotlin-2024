package io.github.endzeitbegins.aoc2023.day09

import io.github.endzeitbegins.aoc2023.checkSolution
import io.github.endzeitbegins.aoc2023.readInput

fun parseNumbers(input: String): List<List<Long>> {
    return input
        .lines()
        .map { line ->
            line
                .split(" ")
                .map(String::toLong)
        }
}

fun List<Long>.predictNextValue(): Long {
    return this
        .calculateSteps()
        .sumOf { rowStep -> rowStep.last() }
}

fun List<Long>.predictPreviousValue(): Long {
    return this
        .calculateSteps()
        .foldRight(0) { row, acc ->
            row.first() - acc
        }
}

fun List<Long>.calculateSteps(): List<List<Long>> {
    val rows: MutableList<List<Long>> = mutableListOf(this)
    var row: List<Long> = this

    while (row.size > 1) {
        row = row
            .windowed(size = 2, step = 1)
            .map { (lhs, rhs) -> rhs - lhs }
        rows += row
    }

    return rows
}

fun part1(input: String): Long {
    return parseNumbers(input)
        .sumOf { it.predictNextValue() }
}

fun part2(input: String): Long {
    return parseNumbers(input)
        .sumOf { it.predictPreviousValue() }
}


fun main() {
    val testInput = readInput("day09/test-input.txt")
    val input = readInput("day09/input.txt")

    // part 1
    checkSolution(part1(testInput), 114)
    println(part1(input))

    // part 2
    checkSolution(part2(testInput), 2)
    println(part2(input))
}
