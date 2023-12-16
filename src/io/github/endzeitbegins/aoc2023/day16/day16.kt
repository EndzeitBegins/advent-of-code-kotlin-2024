package io.github.endzeitbegins.aoc2023.day16

import io.github.endzeitbegins.aoc2023.checkSolution
import io.github.endzeitbegins.aoc2023.day16.Direction.*
import io.github.endzeitbegins.aoc2023.readInput

enum class Direction {
    UP, DOWN, LEFT, RIGHT
}

class Contraption(
    val grid: CharArray,
    val height: Int,
) {

    val width: Int = grid.size / height

    companion object {
        fun from(input: String): Contraption {
            val lines = input.lines()

            val height = lines.size
            val width = lines.first().length
            val grid = CharArray(height * width)

            lines.forEachIndexed { y, line ->
                line.forEachIndexed { x, char ->
                    grid[x + y * width] = char
                }
            }

            return Contraption(
                grid = grid,
                height = height,
            )
        }
    }

    operator fun CharArray.get(x: Int, y: Int): Char =
        this[indexFor(x, y)]

    operator fun BooleanArray.get(x: Int, y: Int): Boolean =
        this[indexFor(x, y)]
    operator fun BooleanArray.set(x: Int, y: Int, value: Boolean) {
        this[indexFor(x, y)] = value
    }

    fun indexFor(x: Int, y: Int): Int = x + y * width

    fun isInGrid(x: Int, y: Int): Boolean {
        return x in 0 until width
                && y in 0 until height
    }
}

data class BeamPosition(
    val x: Int,
    val y: Int,
    val direction: Direction,
)

fun Contraption.nextBeamPositions(beamPosition: BeamPosition): List<BeamPosition> {
    val (x, y, direction) = beamPosition

    return when(val cellType = grid[x, y]) {
        '.' -> {
            when(direction) {
                UP -> listOf(upFrom(beamPosition))
                DOWN -> listOf(downFrom(beamPosition))
                LEFT -> listOf(leftFrom(beamPosition))
                RIGHT -> listOf(rightFrom(beamPosition))
            }
        }
        '/' -> {
            when(direction) {
                UP -> listOf(rightFrom(beamPosition))
                DOWN -> listOf(leftFrom(beamPosition))
                LEFT -> listOf(downFrom(beamPosition))
                RIGHT -> listOf(upFrom(beamPosition))
            }
        }
        '\\' -> {
            when(direction) {
                UP -> listOf(leftFrom(beamPosition))
                DOWN -> listOf(rightFrom(beamPosition))
                LEFT -> listOf(upFrom(beamPosition))
                RIGHT -> listOf(downFrom(beamPosition))
            }
        }
        '|' -> {
            when(direction) {
                UP -> listOf(upFrom(beamPosition))
                DOWN -> listOf(downFrom(beamPosition))
                LEFT -> listOf(upFrom(beamPosition), downFrom(beamPosition))
                RIGHT -> listOf(upFrom(beamPosition), downFrom(beamPosition))
            }
        }
        '-' -> {
            when(direction) {
                UP -> listOf(leftFrom(beamPosition), rightFrom(beamPosition))
                DOWN -> listOf(leftFrom(beamPosition), rightFrom(beamPosition))
                LEFT -> listOf(leftFrom(beamPosition))
                RIGHT -> listOf(rightFrom(beamPosition))
            }
        }

        else -> error("Unsupported cell type $cellType")
    }
}

private fun upFrom(position: BeamPosition) =
    position.copy(y = position.y - 1, direction = UP)

private fun downFrom(position: BeamPosition) =
    position.copy(y = position.y + 1, direction = DOWN)


private fun leftFrom(position: BeamPosition) =
    position.copy(x = position.x - 1, direction = LEFT)

private fun rightFrom(position: BeamPosition) =
    position.copy(x = position.x + 1, direction = RIGHT)

fun Contraption.countEnergizedCells(startingBeam: BeamPosition): Int {
    val energizedCells = mutableSetOf<Int>()
    val followedBeams = mutableSetOf<BeamPosition>()

    val beamsToFollow = mutableListOf<BeamPosition>()
    beamsToFollow += startingBeam

    while (beamsToFollow.isNotEmpty()) {
        val beamToFollow = beamsToFollow.removeFirst()

        if (beamToFollow in followedBeams) {
            // another beam passed this way; no need to follow again
            continue
        }
        if (!isInGrid(beamToFollow.x, beamToFollow.y)) {
            // beam left grid; no need to follow
            continue
        }

        followedBeams += beamToFollow
        energizedCells += indexFor(beamToFollow.x, beamToFollow.y)

        beamsToFollow += nextBeamPositions(beamToFollow)
    }

    return energizedCells.size
}

fun Contraption.printEnergizedCells(energizedCells: Set<Int>): String {
    return buildString {
        for (y in 0 until height) {
            for (x in 0 until width) {
                append(
                    if (indexFor(x, y) in energizedCells) '#' else '.'
                )
            }
            appendLine()
        }
    }
}

fun part1(input: String): Int {
    val contraption = Contraption.from(input)
    val startPosition = BeamPosition(0, 0, RIGHT)

    return contraption.countEnergizedCells(startPosition)
}

fun part2(input: String): Int {
    val contraption = Contraption.from(input)

    val horizontalStartPositions = (0 until contraption.width)
        .flatMap { x ->
            listOf(
                BeamPosition(x = x, y = 0, DOWN),
                BeamPosition(x = x, y = contraption.height - 1, UP)
            )
        }
    val verticalStartPositions = (0 until contraption.height)
        .flatMap { y ->
            listOf(
                BeamPosition(x = 0, y = y, RIGHT),
                BeamPosition(x = contraption.width - 1, y = y, LEFT)
            )
        }

    val startPositions = horizontalStartPositions + verticalStartPositions

    return startPositions
        .maxOf { startPosition -> contraption.countEnergizedCells(startPosition) }
}

fun main() {
    val testInput = readInput("day16/test-input.txt")
    val input = readInput("day16/input.txt")

    // part 1
    checkSolution(part1(testInput), 46)
    println(part1(input))

    // part 2
     checkSolution(part2(testInput), 51)
     println(part2(input))
}
