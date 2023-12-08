package io.github.endzeitbegins.aoc2023.day08

import io.github.endzeitbegins.aoc2023.checkSolution
import io.github.endzeitbegins.aoc2023.lcm
import io.github.endzeitbegins.aoc2023.readInput

enum class Direction {
    LEFT, RIGHT
}

data class ParsedMap(
    val directions: Sequence<Direction>,
    val nodes: Map<String, Pair<String, String>>,
)

fun parseMap(input: String): ParsedMap {
    val lines = input.lines()

    val rawDirections = lines
        .first()
        .map { char ->
            when(char) {
                'L' -> Direction.LEFT
                'R' -> Direction.RIGHT
                else -> TODO()
            }
        }
    val directions = sequence {
        while(true) {
            yieldAll(rawDirections)
        }
    }

    val nodes = lines
        .drop(2)
        .associate { line ->
            val key = line.take(3)
            val left = line.substring(7, 10)
            val right = line.substring(12, 15)

            key to (left to right)
        }

    return ParsedMap(
        directions = directions,
        nodes = nodes,
    )
}

private fun ParsedMap.countSteps(): Long {
    var position = "AAA"
    var steps: Long = 0

    for (direction in directions) {
        val options = nodes.getValue(position)
        position = when(direction) {
            Direction.LEFT -> options.first
            Direction.RIGHT -> options.second
        }
        steps += 1

        if (position == "ZZZ") {
            return steps
        }
    }

    TODO()
}

private fun ParsedMap.countGhostSteps(startPosition: String): Long {
    var position = startPosition
    var steps: Long = 0

    for (direction in directions) {
        val options = nodes.getValue(position)
        position = when(direction) {
            Direction.LEFT -> options.first
            Direction.RIGHT -> options.second
        }
        steps += 1

        if (position.endsWith('Z')) {
            return steps
        }
    }

    TODO()
}

private fun ParsedMap.countTotalGhostSteps(): Long {
    val startPositions = nodes.keys.filter { position -> position.endsWith('A') }

    val minimalStepsEach = startPositions
        .map { startPosition -> countGhostSteps(startPosition) }

    return lcm(minimalStepsEach.toSet())
}

fun part1(input: String): Long {
    return parseMap(input).countSteps()
}

fun part2(input: String): Long {
    return parseMap(input).countTotalGhostSteps()
}

fun main() {
    val testInput = readInput("day08/test-input.txt")
    val testInput2 = readInput("day08/test-input2.txt")
    val testInput3 = readInput("day08/test-input3.txt")
    val input = readInput("day08/input.txt")

    // part 1
    checkSolution(part1(testInput), 2)
    checkSolution(part1(testInput2), 6)
    println(part1(input))

    // part 2
     checkSolution(part2(testInput3), 6)
     println(part2(input))
}
