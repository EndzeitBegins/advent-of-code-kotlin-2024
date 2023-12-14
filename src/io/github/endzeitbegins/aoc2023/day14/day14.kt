package io.github.endzeitbegins.aoc2023.day14

import io.github.endzeitbegins.aoc2023.checkSolution
import io.github.endzeitbegins.aoc2023.readInput

data class RocksSlidingTo(
    val index: Int,
    val count: Int,
)

fun RocksSlidingTo.calculateLoad(dishHeight: Int): Int {
    var load = 0

    repeat(count) { rockIndex ->
        val rockLoad = dishHeight - (index + 1) - rockIndex
        load += rockLoad
    }

    return load
}

fun part1(input: String): Int {
    val lines = input.lines()
    val slidingRocks = mutableListOf<RocksSlidingTo>()

    val height = lines.size
    val width = lines.first().length

    for(x in 0 until width) {
        var index = -1
        var rocks = 0

        for (y in 0 until height) {
            val cell = lines[y][x]

            if (cell == 'O') {
                rocks += 1
            } else if (cell == '#') {
                if (rocks > 0) {
                    slidingRocks += RocksSlidingTo(
                        index = index,
                        count = rocks,
                    )
                }

                index = y
                rocks = 0
            }
        }

        if (rocks > 0) {
            slidingRocks += RocksSlidingTo(
                index = index,
                count = rocks,
            )
        }
    }

    return slidingRocks
        .sumOf { slidingRock -> slidingRock.calculateLoad(height) }
}


fun part2(input: String): Int {
    return input.length
}

fun main() {
    val testInput = readInput("day14/test-input.txt")
    val input = readInput("day14/input.txt")

    // part 1
    checkSolution(part1(testInput), 136)
    println(part1(input))

    // part 2
    // checkSolution(part2(testInput), 64)
    // println(part2(input))
}
