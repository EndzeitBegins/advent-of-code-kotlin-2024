package io.github.endzeitbegins.aoc2024.day10

import io.github.endzeitbegins.aoc2024.checkSolution
import io.github.endzeitbegins.aoc2024.readInput

private data class Position(val x: Int, val y: Int)

private fun Position.up(): Position = Position(x, y - 1)
private fun Position.down(): Position = Position(x, y + 1)
private fun Position.left(): Position = Position(x - 1, y)
private fun Position.right(): Position = Position(x + 1, y)

private data class TopographicMap(
    private val rows: List<String>,
) {
    val height = rows.size
    val width = rows[0].length

    val groundPositions: List<Position> = buildList {
        for (y in 0..<height) {
            for (x in 0..<width) {
                if (rows[y][x] == '0') {
                    add(Position(x, y))
                }
            }
        }
    }

    fun getHeightAt(position: Position): Int? = rows.getOrNull(position.y)?.getOrNull(position.x)?.digitToIntOrNull()
}

private fun TopographicMap.determineUphillSteps(position: Position): Set<Position> {
    val height = checkNotNull(getHeightAt(position))

    return listOf(position.left(), position.right(), position.up(), position.down())
        .filter { step -> getHeightAt(step) == height + 1 }
        .toSet()
}

private fun TopographicMap.determineTrailheadPositions(groundPosition: Position): List<Position> {
    var positions = listOf(groundPosition)

    repeat(9) {
        positions = positions
            .flatMap(::determineUphillSteps)
    }

    return positions
}

private fun String.toTopographicMap(): TopographicMap =
    TopographicMap(lines())

private fun part1(input: String): Int {
    val topographicMap = input.toTopographicMap()

    return topographicMap.groundPositions
        .sumOf { groundPosition -> topographicMap.determineTrailheadPositions(groundPosition).toSet().size }
}

private fun part2(input: String): Int {
    val topographicMap = input.toTopographicMap()

    return topographicMap.groundPositions
        .sumOf { groundPosition -> topographicMap.determineTrailheadPositions(groundPosition).size }
}

fun main() {
    val testInput = readInput("day10/test-input.txt")
    val testInput2 = readInput("day10/test-input2.txt")
    val input = readInput("day10/input.txt")

    // part 1
    checkSolution(part1(testInput), 2)
    checkSolution(part1(testInput2), 36)
    println(part1(input))

    // part 2
    checkSolution(part2(testInput2), 81)
    println(part2(input))
}
