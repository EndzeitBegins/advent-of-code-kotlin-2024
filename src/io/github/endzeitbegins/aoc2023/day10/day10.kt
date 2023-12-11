package io.github.endzeitbegins.aoc2023.day10

import io.github.endzeitbegins.aoc2023.Position
import io.github.endzeitbegins.aoc2023.checkSolution
import io.github.endzeitbegins.aoc2023.readInput

enum class Tile(val connectedTo: Set<Direction>) {
    PIPE_NS(setOf(Direction.NORTH, Direction.SOUTH)),
    PIPE_EW(setOf(Direction.EAST, Direction.WEST)),
    BEND_NE(setOf(Direction.NORTH, Direction.EAST)),
    BEND_NW(setOf(Direction.NORTH, Direction.WEST)),
    BEND_SW(setOf(Direction.SOUTH, Direction.WEST)),
    BEND_SE(setOf(Direction.SOUTH, Direction.EAST)),
    GROUND(emptySet()),
    START(setOf(Direction.NORTH, Direction.EAST, Direction.SOUTH, Direction.WEST));

    companion object {
        fun from(c: Char): Tile {
            return when(c) {
                '|' -> Tile.PIPE_NS
                '-' -> Tile.PIPE_EW
                'L' -> Tile.BEND_NE
                'J' -> Tile.BEND_NW
                '7' -> Tile.BEND_SW
                'F' -> Tile.BEND_SE
                '.' -> Tile.GROUND
                'S' -> Tile.START
                else -> error("Unsupported tile $c")
            }
        }
    }
}

fun Tile.singleConnectionOrNull(from: Direction): Direction? {
    return connectedTo.singleOrNull { it != from }
}
fun Tile.singleConnection(from: Direction): Direction {
    return connectedTo.single { it != from }
}

enum class Direction {
    NORTH, EAST, SOUTH, WEST
}

class Sketch(input: String) {
    private val lines = input.lines()
    val startPosition: Position = determineStartPosition()
    val minX: Int = 0
    val minY: Int = 0
    val maxX: Int = lines.first().lastIndex
    val maxY: Int = lines.lastIndex

    operator fun get(x: Int, y:Int): Tile {
        return Tile.from(lines[y][x])
    }

    private fun determineStartPosition(): Position {
        lines.forEachIndexed { yIndex, line ->
            val xIndex = line.indexOf('S')

            if (xIndex >= 0) {
                return Position(x = xIndex, y = yIndex)
            }
        }
        error("Couldn't determine start position")
    }
}

fun Sketch.calculateLoop(): MutableList<Position> {
    val loop = mutableListOf(startPosition)

    var (position, direction) = when {
        startPosition.x - 1 >= minX && get(startPosition.x - 1, startPosition.y).singleConnectionOrNull(Direction.EAST) != null -> {
            Position(x = startPosition.x - 1,y = startPosition.y) to Direction.EAST
        }
        startPosition.x + 1 <= maxX && get(startPosition.x + 1, startPosition.y).singleConnectionOrNull(Direction.WEST) != null -> {
            Position(x = startPosition.x + 1,y = startPosition.y) to Direction.WEST
        }
        startPosition.y - 1 >= minY && get(startPosition.x, startPosition.y - 1).singleConnectionOrNull(Direction.SOUTH) != null -> {
            Position(x = startPosition.x,y = startPosition.y - 1) to Direction.SOUTH
        }
        else -> {
            Position(x = startPosition.x,y = startPosition.y + 1) to Direction.NORTH
        }
    }

    while (position != startPosition) {
        loop += position

        val pipe = get(x = position.x, y = position.y)
        direction = pipe.singleConnection(direction)
        position = when(direction) {
            Direction.NORTH -> position.copy(y = position.y - 1)
            Direction.SOUTH -> position.copy(y = position.y + 1)
            Direction.WEST -> position.copy(x = position.x - 1)
            Direction.EAST -> position.copy(x = position.x + 1)
        }
        direction = when(direction) {
            Direction.NORTH -> Direction.SOUTH
            Direction.SOUTH -> Direction.NORTH
            Direction.WEST -> Direction.EAST
            Direction.EAST -> Direction.WEST
        }
    }

    return loop
}

fun part1(input: String): Int {
    val sketch = Sketch(input)
    val loop = sketch.calculateLoop()

    return loop.size / 2 + loop.size % 2
}

fun part2(input: String): Int {
    val sketch = Sketch(input)
    val loop = sketch.calculateLoop()

    return input.length
}

fun main() {
    val testInput = readInput("day10/test-input.txt")
    val testInput2 = readInput("day10/test-input2.txt")
    val input = readInput("day10/input.txt")

    // part 1
    checkSolution(part1(testInput), 8)
    println(part1(input))

    // part 2
     checkSolution(part2(testInput2), 10)
    // println(part2(input))
}
