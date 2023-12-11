package io.github.endzeitbegins.aoc2023.day11

import io.github.endzeitbegins.aoc2023.Position
import io.github.endzeitbegins.aoc2023.checkSolution
import io.github.endzeitbegins.aoc2023.readInput
import kotlin.math.absoluteValue

class Image(input: String) {
    val galaxyPositions: List<Position> = buildList {
        input.lines().forEachIndexed { y, row ->
            row.forEachIndexed { x, cell ->
                if (cell == '#') {
                    add(Position(x = x, y = y))
                }
            }
        }
    }

    private val columnsWithGalaxy: Set<Int> = galaxyPositions.map { it.x }.toSet()
    private val rowsWithGalaxy: Set<Int> = galaxyPositions.map { it.y }.toSet()

    fun distanceBetween(a: Position, b: Position, expansionFactor: Long): Long {
        val xDistance = determineDistanceForDimension(
            aValue = a.x,
            bValue = b.x,
            dimensionIndicesWithGalaxy = columnsWithGalaxy,
            expansionFactor = expansionFactor
        )

        val yDistance = determineDistanceForDimension(
            aValue = a.y,
            bValue = b.y,
            dimensionIndicesWithGalaxy = rowsWithGalaxy,
            expansionFactor = expansionFactor
        )

        return xDistance + yDistance
    }

    private fun determineDistanceForDimension(
        aValue: Int,
        bValue: Int,
        expansionFactor: Long,
        dimensionIndicesWithGalaxy: Set<Int>
    ): Long {
        val min = minOf(aValue, bValue)
        val max = maxOf(aValue, bValue)
        val range = min until max
        val galaxiesInDimension = dimensionIndicesWithGalaxy.filter { it in range }.size
        val emptySpacesInDimension = (max - min) - galaxiesInDimension

        return galaxiesInDimension + expansionFactor * emptySpacesInDimension
    }
}


fun Image.determineGalaxyDistances(expansionFactor: Long = 2) : Long{
    var totalDistance = 0L

    for (i in 0..galaxyPositions.lastIndex) {
        for (j in i + 1..galaxyPositions.lastIndex) {
            totalDistance += distanceBetween(
                a = galaxyPositions[i],
                b = galaxyPositions[j],
                expansionFactor = expansionFactor
            )
        }
    }

    return totalDistance
}

fun part1(image: Image): Long {
    return image.determineGalaxyDistances(expansionFactor = 2)
}

fun part2(image: Image): Long {
    return image.determineGalaxyDistances(expansionFactor = 1000000)
}

fun main() {
    val testInput = readInput("day11/test-input.txt")
    val input = readInput("day11/input.txt")

    val testImage = Image(testInput)
    val image = Image(input)

    // part 1
    checkSolution(part1(testImage), 374)
    println(part1(image))

    // part 2
     checkSolution(testImage.determineGalaxyDistances(expansionFactor = 10), 1030)
     checkSolution(testImage.determineGalaxyDistances(expansionFactor = 100), 8410)
     println(part2(image))
}
