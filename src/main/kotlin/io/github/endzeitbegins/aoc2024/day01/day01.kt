package io.github.endzeitbegins.aoc2024.day01

import io.github.endzeitbegins.aoc2024.checkSolution
import io.github.endzeitbegins.aoc2024.readInputLines
import kotlin.math.abs

private fun part1(input: List<String>): Int {
    val (leftList, rightList) = parseLists(input)

    return leftList.sorted()
        .zip(rightList.sorted())
        .sumOf { (a, b) -> abs(a - b) }
}

private fun part2(input: List<String>): Int {
    val (leftList, rightList) = parseLists(input)

    val leftFrequencies = leftList.toFrequencyMap()
    val rightFrequencies = rightList.toFrequencyMap()

    return leftFrequencies
        .map { (locationId, occurrences) -> locationId * (rightFrequencies[locationId] ?: 0) * occurrences }
        .sum()
}

private fun List<Int>.toFrequencyMap(): Map<Int, Int> =
    groupingBy { it }
        .eachCount()

private fun parseLists(input: List<String>): Pair<MutableList<Int>, MutableList<Int>> {
    val leftList = mutableListOf<Int>()
    val rightList = mutableListOf<Int>()

    for (line in input) {
        val (itemA, itemB) = line.split("""\s+""".toRegex(), 2)
        leftList += itemA.toInt()
        rightList += itemB.toInt()
    }

    return Pair(leftList, rightList)
}

fun main() {
    val testInput = readInputLines("day01/test-input.txt")
    val input = readInputLines("day01/input.txt")

    // part 1
    checkSolution(part1(testInput), 11)
    println(part1(input))

    // part 2
    checkSolution(part2(testInput), 31)
    println(part2(input))
}
