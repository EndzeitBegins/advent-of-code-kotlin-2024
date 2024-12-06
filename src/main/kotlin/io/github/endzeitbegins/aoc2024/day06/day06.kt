package io.github.endzeitbegins.aoc2024.day06

import io.github.endzeitbegins.aoc2024.checkSolution
import io.github.endzeitbegins.aoc2024.readInput

private enum class Direction {
    UP, DOWN, LEFT, RIGHT
}

private fun Direction.turnRight(): Direction = when (this) {
    Direction.UP -> Direction.RIGHT
    Direction.DOWN -> Direction.LEFT
    Direction.LEFT -> Direction.UP
    Direction.RIGHT -> Direction.DOWN
}

private data class Guard(
    val position: Position,
    val direction: Direction,
)

private data class Position(val x: Int, val y: Int)

private data class GuardMap(
    val height: Int,
    val width: Int,
    val obstacles: Set<Position>,
    val initialGuard: Guard,
) {
    operator fun contains(position: Position): Boolean =
        position.x in 0..<width && position.y in 0..<height
}

private fun Guard.takeStep(obstacles: Set<Position>): Guard {
    var direction = direction
    var potentialPosition = position.takeStepTo(direction)

    while (potentialPosition in obstacles) {
        direction = direction.turnRight()
        potentialPosition = position.takeStepTo(direction)
    }

    return Guard(position = potentialPosition, direction = direction)
}

private fun Position.takeStepTo(direction: Direction): Position =
    when (direction) {
        Direction.UP -> Position(x = x, y = y - 1)
        Direction.DOWN -> Position(x = x, y = y + 1)
        Direction.LEFT -> Position(x = x - 1, y = y)
        Direction.RIGHT -> Position(x = x + 1, y = y)
    }

private fun String.toGuardMap(): GuardMap {
    val lines = lines()

    val height = lines.size
    val width = lines[0].length

    val obstacles = mutableSetOf<Position>()
    var guardPosition: Position? = null

    for (x in 0 until width) {
        for (y in 0 until height) {
            val field = lines[y][x]

            when (field) {
                '#' -> obstacles += Position(x, y)
                '^' -> guardPosition = Position(x, y)
            }
        }
    }

    return GuardMap(
        height = height,
        width = width,
        obstacles = obstacles,
        initialGuard = Guard(
            position = checkNotNull(guardPosition),
            direction = Direction.UP,
        ),
    )
}

private val GuardMap.allPositions: Set<Position>
    get() = buildSet {
        for (x in 0..< width) {
            for (y in 0..< height) {
                this += Position(x, y)
            }
        }
    }
private val GuardMap.guardPathIsLoop: Boolean
    get() {
        var guard = initialGuard
        val guardHistory = mutableSetOf<Guard>()

        while (guard.position in this) {
            if (guard in guardHistory)
                return true

            guardHistory += guard
            guard = guard.takeStep(obstacles)
        }

        return false
    }

private fun part1(input: String): Int {
    val guardMap = input.toGuardMap()
    var guard = guardMap.initialGuard
    val guardPositions = mutableSetOf<Position>()

    while (guard.position in guardMap) {
        guardPositions += guard.position

        guard = guard.takeStep(guardMap.obstacles)
    }

    return guardPositions.size
}

private fun part2(input: String): Int {
    val guardMap = input.toGuardMap()

    return guardMap.allPositions
        .filterNot { position -> position in guardMap.obstacles }
        .filterNot { position -> position == guardMap.initialGuard.position }
        .count { additionalObstacle ->
            val adjustedGuardMap = guardMap.copy(obstacles = guardMap.obstacles + additionalObstacle)

            adjustedGuardMap.guardPathIsLoop
        }
}

fun main() {
    val testInput = readInput("day06/test-input.txt")
    val input = readInput("day06/input.txt")

    // part 1
    checkSolution(part1(testInput), 41)
    println(part1(input))

    // part 2
     checkSolution(part2(testInput), 6)
     println(part2(input))
}
