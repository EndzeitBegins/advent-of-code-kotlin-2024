package io.github.endzeitbegins.aoc2023.day10

import io.github.endzeitbegins.aoc2023.checkSolution
import io.github.endzeitbegins.aoc2023.readInput

enum class Direction {
    NORTH, EAST, SOUTH, WEST
}

private fun Direction.toOpposite(): Direction = when (this) {
    Direction.NORTH -> Direction.SOUTH
    Direction.SOUTH -> Direction.NORTH
    Direction.WEST -> Direction.EAST
    Direction.EAST -> Direction.WEST
}

data class Position(val x: Int, val y: Int)

private fun Position.isIn(sketch: Sketch): Boolean =
    x in sketch.minX..sketch.maxX && y in sketch.minY..sketch.maxY

private fun northOf(position: Position) = position.copy(y = position.y - 1)
private fun southOf(position: Position) = position.copy(y = position.y + 1)
private fun westOf(position: Position) = position.copy(x = position.x - 1)
private fun eastOf(position: Position) = position.copy(x = position.x + 1)
private fun Position.nextIn(
    direction: Direction
) = when (direction) {
    Direction.NORTH -> northOf(this)
    Direction.SOUTH -> southOf(this)
    Direction.WEST -> westOf(this)
    Direction.EAST -> eastOf(this)
}

private fun Position.isSurroundedBy(loop: List<Pipe>): Boolean {
    val position = this

    val relevantPipes = loop
        .filter { pipe -> pipe.position.y == position.y && pipe.position.x <= position.x }

    if (relevantPipes.any { pipe -> pipe.position == position }) {
        return false
    }

    val intersections = relevantPipes
        .filterNot { pipe -> pipe.type in setOf(Tile.PIPE_EW, Tile.BEND_NE, Tile.BEND_NW) }
        .size

    return intersections % 2 == 1
}

enum class Tile(val symbol: Char, val connectedTo: Set<Direction>) {
    PIPE_NS(symbol = '|', connectedTo = setOf(Direction.NORTH, Direction.SOUTH)),
    PIPE_EW(symbol = '-', connectedTo = setOf(Direction.EAST, Direction.WEST)),
    BEND_NE(symbol = 'L', connectedTo = setOf(Direction.NORTH, Direction.EAST)),
    BEND_NW(symbol = 'J', connectedTo = setOf(Direction.NORTH, Direction.WEST)),
    BEND_SW(symbol = '7', connectedTo = setOf(Direction.SOUTH, Direction.WEST)),
    BEND_SE(symbol = 'F', connectedTo = setOf(Direction.SOUTH, Direction.EAST)),
    GROUND(symbol = '.', connectedTo = emptySet());

    companion object {
        fun from(symbol: Char): Tile {
            return entries.singleOrNull { it.symbol == symbol }
                ?: error("Unsupported tile $symbol")
        }
    }
}

data class Pipe(val position: Position, val type: Tile)

class Sketch(input: String) {
    private val lines = input.lines()

    val minX: Int = 0
    val minY: Int = 0
    val maxX: Int = lines.first().lastIndex
    val maxY: Int = lines.lastIndex

    val startPosition: Position = determineStartPosition()
    val startTile: Tile = determineStartTile()

    operator fun get(x: Int, y: Int): Tile {
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

    private fun determineStartTile(): Tile {
        val isConnectedToNorth = northOf(startPosition)
            .let { position -> position.isIn(this) && get(position).connectedTo.contains(Direction.SOUTH) }
        val isConnectedToSouth = southOf(startPosition)
            .let { position -> position.isIn(this) && get(position).connectedTo.contains(Direction.NORTH) }
        val isConnectedToWest = westOf(startPosition)
            .let { position -> position.isIn(this) && get(position).connectedTo.contains(Direction.EAST) }
        val isConnectedToEast = eastOf(startPosition)
            .let { position -> position.isIn(this) && get(position).connectedTo.contains(Direction.WEST) }

        val connectedTo = mutableSetOf<Direction>()

        if (isConnectedToNorth) connectedTo += Direction.NORTH
        if (isConnectedToSouth) connectedTo += Direction.SOUTH
        if (isConnectedToWest) connectedTo += Direction.WEST
        if (isConnectedToEast) connectedTo += Direction.EAST

        return Tile.entries.single { it.connectedTo == connectedTo }
    }
}

private fun Sketch.get(position: Position) =
    get(x = position.x, y = position.y)

fun Sketch.calculateLoop(): List<Pipe> {
    fun Tile.singleConnection(from: Direction): Direction {
        return connectedTo.single { it != from }
    }

    val loop = mutableListOf(Pipe(startPosition, startTile))

    var direction = startTile.connectedTo.first()
    var position = startPosition.nextIn(direction)
    direction = direction.toOpposite()

    while (position != startPosition) {
        val tile = get(position)

        loop += Pipe(position, tile)

        direction = tile.singleConnection(from = direction)

        position = position.nextIn(direction)
        direction = direction.toOpposite()
    }

    return loop
}


private fun Sketch.allPositions(): Sequence<Position> =
    sequence {
        for (y in minY..maxY) {
            for (x in minX..maxX) {
                yield(Position(x, y))
            }
        }
    }

fun part1(input: String): Int {
    val sketch = Sketch(input)
    val loop = sketch.calculateLoop()
    val loopSize = loop.size

    return loopSize / 2 + loopSize % 2
}

fun part2(input: String): Int {
    val sketch = Sketch(input)
    val loop = sketch.calculateLoop()

    return sketch
        .allPositions()
        .count { position -> position.isSurroundedBy(loop) }
}

fun main() {
    val testInput = readInput("day10/test-input.txt")
    val testInput2 = readInput("day10/test-input2.txt")
    val testInput3 = readInput("day10/test-input3.txt")
    val testInput4 = readInput("day10/test-input4.txt")
    val input = readInput("day10/input.txt")

    // part 1
    checkSolution(part1(testInput), 8)
    println(part1(input))

    // part 2
    checkSolution(part2(testInput2), 10)
    checkSolution(part2(testInput3), 4)
    checkSolution(part2(testInput4), 8)
    println(part2(input))
}
