package io.github.endzeitbegins.aoc2024.day15

import io.github.endzeitbegins.aoc2024.checkSolution
import io.github.endzeitbegins.aoc2024.readInput

private data class Position(val x: Int, val y: Int)

private enum class Direction {
    UP, DOWN, LEFT, RIGHT;

    companion object {
        fun from(char: Char): Direction = when (char) {
            '<' -> LEFT
            '>' -> RIGHT
            '^' -> UP
            'v' -> DOWN
            else -> error("Unsupported character: $char")
        }
    }
}

private typealias MovementPlan = List<Direction>

private data class WarehouseMap(val positions: Map<Position, Char>) {
    val roboterPosition: Position =
        positions.entries.single { (_, char) -> char == '@' }.key
}

private val WarehouseMap.boxPositions: List<Position>
    get() = positions.entries
         .filter { (_, char) -> char == 'O' }
         .map { (position, _) -> position }

private fun Position.intoDirection(direction: Direction, steps: Int): Position = when (direction) {
    Direction.UP -> Position(this.x, this.y - steps)
    Direction.DOWN -> Position(this.x, this.y + steps)
    Direction.LEFT -> Position(this.x - steps, this.y)
    Direction.RIGHT -> Position(this.x + steps, this.y)
}

private fun Map<Position, Char>.toPrintout(): String = buildString {
    val width = this@toPrintout.maxOf { (position, _) -> position.x }
    val height = this@toPrintout.maxOf { (position, _) -> position.y }

    for (y in 0 .. height) {
        for (x in 0 .. width) {
            append(this@toPrintout[Position(x, y)])
        }
        appendLine()
    }
}

private fun WarehouseMap.withMovements(movementPlan: MovementPlan): WarehouseMap {
    val positions = positions.toMutableMap()
    var roboterPosition: Position = roboterPosition

    fun stepsUntilFreeSpaceOrNull(position: Position, direction: Direction): Int? {
        var steps = 1

        while (true) {
            val itemAtPosition = positions.getValue(position.intoDirection(direction, steps))

            when (itemAtPosition) {
                '#' -> return null
                '.' -> return steps
                else -> steps += 1
            }
        }
    }

    for (direction in movementPlan) {
        // println(positions.toPrintout())

        val stepsUntilFreeSpace = stepsUntilFreeSpaceOrNull(roboterPosition, direction)
            ?: continue

        repeat(stepsUntilFreeSpace) { index ->
            val steps = stepsUntilFreeSpace - index

            val targetPosition = roboterPosition.intoDirection(direction, steps)
            val sourcePosition = roboterPosition.intoDirection(direction, steps - 1)

            positions[targetPosition] = positions.getValue(sourcePosition)
        }

        positions[roboterPosition] = '.'
        roboterPosition = roboterPosition.intoDirection(direction, 1)
    }

    return WarehouseMap(positions)
}

private fun parseWarehouseMap(warehouse: String): WarehouseMap =
    WarehouseMap(buildMap {
        for ((y, row) in warehouse.lineSequence().withIndex()) {
            for ((x, item) in row.withIndex()) {
                set(Position(x, y), item)
            }
        }
    })

private fun parseInput(input: String): Pair<WarehouseMap, MovementPlan> {
    val (warehouse, movements) = input.split("\n\n", limit = 2)

    val warehouseMap = parseWarehouseMap(warehouse)

    val movementPlan: MovementPlan = movements.asSequence()
        .filterNot(Char::isWhitespace)
        .map(Direction.Companion::from)
        .toList()

    return warehouseMap to movementPlan
}

private fun Position.toGpsCoordinate(): Long =
    y * 100L + x

private fun part1(input: String): Long {
    val (warehouseMap, movementPlan) = parseInput(input)

    val updatedWarehouseMap = warehouseMap.withMovements(movementPlan)

    return updatedWarehouseMap.boxPositions
        .sumOf(Position::toGpsCoordinate)
}

private fun part2(input: String): Long {
    return input.length * 1L
}

fun main() {
    val testInput = readInput("day15/test-input.txt")
    val testInput2 = readInput("day15/test-input2.txt")
    val input = readInput("day15/input.txt")

    // part 1
    checkSolution(part1(testInput), 2028)
    checkSolution(part1(testInput2), 10092)
    println(part1(input))

    // part 2
    checkSolution(part2(testInput), 9021)
    println(part2(input))
}
