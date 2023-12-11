package io.github.endzeitbegins.aoc2023.day11

import io.github.endzeitbegins.aoc2023.Position
import io.github.endzeitbegins.aoc2023.checkSolution
import io.github.endzeitbegins.aoc2023.readInput
import kotlin.math.absoluteValue

class Image(input: String) {
    val galaxyPositions: List<Position>

    init {
        galaxyPositions = buildList {
            input.lines().forEachIndexed { y, row ->
                row.forEachIndexed { x, cell ->
                    if (cell == '#') {
                        add(Position(x = x, y = y))
                    }
                }
            }
        }
    }

    private val columnsWithGalaxy: Set<Int> = galaxyPositions.map { it.x }.toSet()
    private val rowsWithGalaxy: Set<Int> = galaxyPositions.map { it.y }.toSet()

    fun distanceBetween(a: Position, b: Position, expansionFactor: Long): Long {
        val minX = minOf(a.x, b.x)
        val maxX = maxOf(a.x, b.x)
        val xRange = minX until maxX
        val galaxiesInRow = columnsWithGalaxy.filter { it in xRange }.size
        val emptySpacesInRow = (maxX - minX) - galaxiesInRow
        val xDistance = galaxiesInRow + expansionFactor * emptySpacesInRow

        val minY = minOf(a.y, b.y)
        val maxY = maxOf(a.y, b.y)
        val yRange = minY until maxY
        val galaxiesInColumn = rowsWithGalaxy.filter { it in yRange }.size
        val emptySpacesInColumn = (maxY - minY) - galaxiesInColumn
        val yDistance = galaxiesInColumn + expansionFactor * emptySpacesInColumn

        return xDistance + yDistance
    }
}



fun List<String>.expand(): List<List<Char>> {
    val rowsWithGalaxy = mutableSetOf<Int>()
    val columnsWithGalaxy = mutableSetOf<Int>()

    this.forEachIndexed { y, row ->
        row.forEachIndexed { x, cell ->
            if (cell == '#') {
                rowsWithGalaxy.add(y)
                columnsWithGalaxy.add(x)
            }
        }
    }

    val blankRow = List(2 * first().length - columnsWithGalaxy.size) { '.' }

    val expandedRows = mutableListOf<List<Char>>()

    this.forEachIndexed { y, row ->
        if (y in rowsWithGalaxy) {
            val expandedCells = mutableListOf<Char>()

            row.forEachIndexed { x, cell ->
                if (x in columnsWithGalaxy) {
                    expandedCells.add(cell)
                } else {
                    expandedCells.add('.')
                    expandedCells.add('.')
                }
            }

            expandedRows.add(expandedCells)

        } else {
            expandedRows.add(blankRow)
            expandedRows.add(blankRow)
        }
    }

    return expandedRows
}

fun List<List<Char>>.detectGalaxies(): List<Position> {
    val galaxyPositions = mutableListOf<Position>()
    
    this.forEachIndexed { y, row -> 
        row.forEachIndexed { x, cell -> 
            if (cell == '#') {
                galaxyPositions.add(
                    Position(x = x, y = y)
                )
            }
        }
    }
    
    return galaxyPositions
}

fun Position.distanceTo(other: Position): Int {
    val xDistance = (this.x - other.x).absoluteValue
    val yDistance = (this.y - other.y).absoluteValue

    return xDistance + yDistance
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
