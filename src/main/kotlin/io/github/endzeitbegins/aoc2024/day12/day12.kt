package io.github.endzeitbegins.aoc2024.day12

import io.github.endzeitbegins.aoc2024.checkSolution
import io.github.endzeitbegins.aoc2024.readInput

private data class Position(val x: Int, val y: Int)

private fun Position.up(): Position = Position(x, y - 1)
private fun Position.down(): Position = Position(x, y + 1)
private fun Position.left(): Position = Position(x - 1, y)
private fun Position.right(): Position = Position(x + 1, y)

private enum class Direction {
    UP, DOWN, LEFT, RIGHT
}

private val Position.neighbours
    get() = setOf(up(), down(), left(), right())

private typealias GardenPlot = Set<Position>

private val GardenPlot.area: Int
    get() = size

private val GardenPlot.perimeter: Int
    get() = sumOf { gardenPlotPosition ->
        gardenPlotPosition.neighbours.count { position -> position !in this }
    }

private val GardenPlot.sides: Int
    get() = Direction.entries.sumOf { direction -> calculateSides(direction) }

private fun GardenPlot.calculateSides(direction: Direction): Int {
    val neighbourPosition: Position.() -> Position = when (direction) {
        Direction.UP -> Position::up
        Direction.DOWN -> Position::down
        Direction.LEFT -> Position::left
        Direction.RIGHT -> Position::right
    }
    val directionCoordinate: Position.() -> Int = when (direction) {
        Direction.UP, Direction.DOWN -> Position::y
        Direction.LEFT, Direction.RIGHT -> Position::x
    }
    val coordinatePerpendicularToDirection: Position.() -> Int = when (direction) {
        Direction.UP, Direction.DOWN -> Position::x
        Direction.LEFT, Direction.RIGHT -> Position::y
    }

    return filterNot { position -> position.neighbourPosition() in this }
        .groupBy(directionCoordinate)
        .map { (_, positions) ->
            positions
                .sortedBy(coordinatePerpendicularToDirection)
                .zipWithNext()
                .count { (positionA, positionB) ->
                    positionA.coordinatePerpendicularToDirection() < positionB.coordinatePerpendicularToDirection() - 1
                } + 1
        }
        .sum()
}

private data class GardenMap(
    private val rows: List<String>,
) {
    val height = rows.size
    val width = rows[0].length

    operator fun get(position: Position): Char = rows[position.y][position.x]

    operator fun contains(position: Position): Boolean =
        position.x in 0..<width && position.y in 0..<height
}

private fun GardenMap.gardenPlotAt(position: Position): GardenPlot = buildSet {
    val gardenMap = this@gardenPlotAt
    val plantType = gardenMap[position]

    val positionsToCheck = mutableListOf(position)
    while (positionsToCheck.isNotEmpty()) {
        val positionToCheck = positionsToCheck.removeFirst()

        if (positionToCheck !in this && gardenMap[positionToCheck] == plantType) {
            this += positionToCheck

            for (neighbour in positionToCheck.neighbours) {
                if (neighbour !in positionsToCheck && neighbour !in this && neighbour in gardenMap)
                    positionsToCheck += neighbour
            }
        }
    }
}

private fun GardenMap.determineGardenPlots(): Set<GardenPlot> {
    val gardenPlots = mutableSetOf<GardenPlot>()
    val usedPositions = mutableSetOf<Position>()

    for (x in 0..<width) {
        for (y in 0..<height) {
            val position = Position(x, y)

            if (position !in usedPositions) {
                val gardenPlot = gardenPlotAt(position)
                usedPositions += gardenPlot
                gardenPlots += gardenPlot
            }
        }
    }

    return gardenPlots
}

private fun String.toGardenMap(): GardenMap =
    GardenMap(lines())

private fun part1(input: String): Int =
    input.toGardenMap()
        .determineGardenPlots()
        .sumOf { gardenPlot -> gardenPlot.area * gardenPlot.perimeter }

private fun part2(input: String): Int =
    input.toGardenMap()
        .determineGardenPlots()
        .sumOf { gardenPlot -> gardenPlot.area * gardenPlot.sides }

fun main() {
    val testInput = readInput("day12/test-input.txt")
    val testInput2 = readInput("day12/test-input2.txt")
    val testInput3 = readInput("day12/test-input3.txt")
    val testInput4 = readInput("day12/test-input4.txt")
    val testInput5 = readInput("day12/test-input5.txt")
    val input = readInput("day12/input.txt")

    // part 1
    checkSolution(part1(testInput), 140)
    checkSolution(part1(testInput2), 772)
    checkSolution(part1(testInput3), 1930)
    println(part1(input))

    // part 2
    checkSolution(part2(testInput), 80)
    checkSolution(part2(testInput2), 436)
    checkSolution(part2(testInput4), 236)
    checkSolution(part2(testInput5), 368)
    checkSolution(part2(testInput3), 1206)
    println(part2(input))
}
