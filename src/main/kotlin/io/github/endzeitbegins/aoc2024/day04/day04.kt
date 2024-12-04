package io.github.endzeitbegins.aoc2024.day04

import io.github.endzeitbegins.aoc2024.checkSolution
import io.github.endzeitbegins.aoc2024.readInput

private val xmasRegex = """XMAS""".toRegex()
private val samxRegex = """SAMX""".toRegex()

private fun part1(input: String): Int {
    val horizontal = input.countWordMatches()
    val vertical = input.rotateNinetyDegrees().countWordMatches()
    val diagonalA = input.rotateFortyFiveDegreesLeft().countWordMatches()
    val diagonalB = input.rotateFortyFiveDegreesRight().countWordMatches()

    return horizontal + vertical + diagonalA + diagonalB
}

private fun String.rotateNinetyDegrees(): String {
    val lines = lines()

    return buildString {
        for (x in 0..lines[0].lastIndex) {
            for (y in 0..lines.lastIndex) {
                append(lines[y][x])
            }

            appendLine()
        }
    }
}

private fun String.rotateFortyFiveDegreesLeft(): String {
    val lines = lines()

    val maxX = lines[0].lastIndex
    val maxY = lines.lastIndex

    val allowedX = 0..maxX
    val allowedY = 0..maxY

    // 5 entries
    // 0 1 2 3 4
    // 4 3 2 1 0 -1 -2 -3 -4
    // 6 entries
    // 0 1 2 3 4 5
    // 5 4 3 2 1 0 -1 -2 -3 -4 -5

    return buildString {
        for (x in maxX downTo -maxX) {
            for (y in 0..maxY) {
                val combinedX = x + y

                if (combinedX !in allowedX) continue
                if (y !in allowedY) continue

                append(lines[y][combinedX])
            }

            appendLine()
        }
    }
}

private fun String.rotateFortyFiveDegreesRight(): String {
    val lines = lines()

    val maxX = lines[0].lastIndex
    val maxY = lines.lastIndex

    val allowedX = 0..maxX
    val allowedY = 0..maxY

    // 5 entries
    // 0 1 2 3 4
    // 0 1 2 3 4 5 6 7 8

    // 6 entries
    // 0 1 2 3 4 5
    // 0 1 2 3 4 5 6 7 8 9 10

    return buildString {
        for (x in 0..maxX + maxX) {
            for (y in 0..maxY) {
                val combinedX = x - y

                if (combinedX !in allowedX) continue
                if (y !in allowedY) continue

                append(lines[y][combinedX])
            }

            appendLine()
        }
    }
}

private fun String.countWordMatches(): Int =
    lineSequence().sumOf { line ->
        line.countMatches(xmasRegex) + line.countMatches(samxRegex)
    }

private fun String.countMatches(regex: Regex): Int =
    splitToSequence(regex).count() - 1

private fun part2(input: String): Int {
    val lines = input.lines()

    var count = 0
    for (x in 0..lines[0].lastIndex - 2) {
        for (y in 0..lines.lastIndex - 2) {
            val middle = lines[y + 1][x + 1]
            val upperLeft = lines[y][x]
            val upperRight = lines[y][x + 2]
            val lowerLeft = lines[y + 2][x]
            val lowerRight = lines[y + 2][x + 2]

            val isXMas = middle == 'A' // middle is an A
                    && (upperLeft == 'M' && lowerRight == 'S' || upperLeft == 'S' && lowerRight == 'M')
                    && (upperRight == 'M' && lowerLeft == 'S' || upperRight == 'S' && lowerLeft == 'M')

            if (isXMas) {
                count += 1
            }
        }
    }
    return count
}

fun main() {
    val testInput = readInput("day04/test-input.txt")
    val input = readInput("day04/input.txt")

    // part 1
    checkSolution(part1(testInput), 18)
    println(part1(input))

    // part 2
    checkSolution(part2(testInput), 9)
    println(part2(input))
}
