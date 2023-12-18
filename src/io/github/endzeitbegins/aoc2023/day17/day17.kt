package io.github.endzeitbegins.aoc2023.day17

import io.github.endzeitbegins.aoc2023.checkSolution
import io.github.endzeitbegins.aoc2023.readInput

class CityMap(
    val cityBlocks: IntArray,
    val height: Int,
) {

    val width: Int = cityBlocks.size / height

    companion object {
        fun from(input: String): CityMap {
            val lines = input.lines()

            val height = lines.size
            val width = lines.first().length
            val cityBlocks = IntArray(height * width)

            lines.forEachIndexed { y, line ->
                line.forEachIndexed { x, char ->
                    cityBlocks[x + y * width] = char.digitToInt()
                }
            }

            return CityMap(
                cityBlocks = cityBlocks,
                height = height,
            )
        }
    }

    fun getHeatLoss(positionIndex: Int): Int =
        cityBlocks[positionIndex]

    fun northOf(positionIndex: Int): Int? {
        if (!isInCity(positionIndex)) {
            return null
        }

        val x = xFor(positionIndex)
        val y = yFor(positionIndex)

        if (y <= 0) {
            return null
        }

        return indexFor(x, y - 1)
    }

    fun westOf(positionIndex: Int): Int? {
        if (!isInCity(positionIndex)) {
            return null
        }

        val x = xFor(positionIndex)
        val y = yFor(positionIndex)

        if (x <= 0) {
            return null
        }

        return indexFor(x - 1, y)
    }

    fun eastOf(positionIndex: Int): Int? {
        if (!isInCity(positionIndex)) {
            return null
        }

        val x = xFor(positionIndex)
        val y = yFor(positionIndex)

        if (x >= width - 1) {
            return null
        }

        return indexFor(x + 1, y)
    }

    fun southOf(positionIndex: Int): Int? {
        if (!isInCity(positionIndex)) {
            return null
        }

        val x = xFor(positionIndex)
        val y = yFor(positionIndex)

        if (y >= height - 1) {
            return null
        }

        return indexFor(x, y + 1)
    }

    private fun isInCity(positionIndex: Int) = positionIndex in cityBlocks.indices

    fun indexFor(x: Int, y: Int): Int = x + y * width
    fun xFor(positionIndex: Int): Int {
        return positionIndex % width
    }
    fun yFor(positionIndex: Int): Int {
        return positionIndex / width
    }

}

class PathNode(
    parent: PathNode?,
    val blockPositionIndex: Int,
    val blockHeatLoss: Int,
) {
    val visitedPositions: List<Int> = parent?.visitedPositions?.plus(blockPositionIndex)
        ?: listOf(blockPositionIndex)
    val totalHeatLoss: Int = parent?.totalHeatLoss?.plus(blockHeatLoss)
        ?: blockHeatLoss
}

fun CityMap.findShortestPath(startIndex: Int, destinationIndex: Int): Int {
    var minimumHeatLoss = Int.MAX_VALUE
    val partialPaths = mutableListOf(
        PathNode(parent = null, blockPositionIndex = startIndex, blockHeatLoss = 0)
    )

    while (partialPaths.isNotEmpty()) {
        val partialPath = partialPaths.removeFirst()

        if (partialPath.blockPositionIndex == destinationIndex) {
            minimumHeatLoss = minOf(minimumHeatLoss, partialPath.totalHeatLoss)
            continue
        }

        val neighborPositions = listOfNotNull(
            northOf(partialPath.blockPositionIndex),
            eastOf(partialPath.blockPositionIndex),
            southOf(partialPath.blockPositionIndex),
            westOf(partialPath.blockPositionIndex),
        )

        val possiblePaths = neighborPositions
            .filterNot { position -> position in partialPath.visitedPositions }
            .mapNotNull { position ->
                val node = PathNode(parent = partialPath, blockPositionIndex = position, blockHeatLoss = getHeatLoss(position))

                when {
                    node.totalHeatLoss >= minimumHeatLoss -> null
                    hasFourBlocksInAStraight(node) -> null
                    else -> node
                }
            }

        partialPaths += possiblePaths
    }

    return minimumHeatLoss
}

private fun CityMap.hasFourBlocksInAStraight(pathNode: PathNode): Boolean {
    if (pathNode.visitedPositions.size < 4) {
        return false
    }

    val relevantPositions = pathNode.visitedPositions.takeLast(4)

    return when {
        relevantPositions.map { positionIndex -> xFor(positionIndex) }.toSet().size == 1 -> true
        relevantPositions.map { positionIndex -> yFor(positionIndex) }.toSet().size == 1 -> true
        else -> false
    }
}

fun part1(input: String): Int {
    val map = CityMap.from(input)

    return map.findShortestPath(0, map.cityBlocks.lastIndex)
}

fun part2(input: String): Int {
    return input.length
}

fun main() {
    val testInput = readInput("day17/test-input.txt")
    val input = readInput("day17/input.txt")

    // part 1
    checkSolution(part1(testInput), 102)
    println(part1(input))

    // part 2
    // checkSolution(part2(testInput), 1)
    // println(part2(input))
}
