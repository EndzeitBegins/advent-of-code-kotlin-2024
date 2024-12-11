package io.github.endzeitbegins.aoc2024.day11

import io.github.endzeitbegins.aoc2024.checkSolution
import io.github.endzeitbegins.aoc2024.readInput


private fun part1(input: String): Long {
    var stones = listStones(input)

    repeat(25) {
        stones = stones.blink()
    }

    return stones.countStones()
}

private fun part2(input: String): Long {
    var stones = listStones(input)

    repeat(75) {
        stones = stones.blink()
    }

    return stones.countStones()
}

private typealias StoneListing = Map<Long, Long>

private fun listStones(input: String): StoneListing =
    input.split(" ")
        .groupingBy(String::toLong)
        .eachCount()
        .mapValues { (_, count) -> count.toLong() }

private fun StoneListing.countStones(): Long =
    values.sum()

private fun StoneListing.blink(): StoneListing {
    val updatedStoneListing = mutableMapOf<Long, Long>()

    for ((stone, count) in this) {
        val derivedStones: List<Long> = deriveStones(stone)

        for (derivedStone in derivedStones) {
            updatedStoneListing[derivedStone] =
                count + updatedStoneListing.getOrDefault(derivedStone, 0)
        }
    }

    return updatedStoneListing
}

private fun deriveStones(stone: Long): List<Long> {
    if (stone == 0L)
        return listOf(1)

    val stoneString = "$stone"
    val digitsOnStone = stoneString.length

    return if (digitsOnStone % 2 == 0) {
        listOf(
            stoneString.take(digitsOnStone / 2).toLong(),
            stoneString.drop(digitsOnStone / 2).toLong()
        )
    } else listOf(stone * 2_024)
}

fun main() {
    val testInput = readInput("day11/test-input.txt")
    val input = readInput("day11/input.txt")

    // part 1
    checkSolution(part1(testInput), 55312L)
    println(part1(input))

    // part 2
    println(part2(input))
}
