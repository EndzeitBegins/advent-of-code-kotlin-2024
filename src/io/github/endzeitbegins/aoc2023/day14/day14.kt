package io.github.endzeitbegins.aoc2023.day14

import io.github.endzeitbegins.aoc2023.checkSolution
import io.github.endzeitbegins.aoc2023.readInput

class ReflectorDish(
    val roundedRockPositions: BooleanArray,
    val cubeShapedRockPositions: BooleanArray,
    val height: Int,
    val width: Int,
) {
    companion object {
        fun from(input: String): ReflectorDish {
            val lines = input.lines()

            val height = lines.size
            val width = lines.first().length
            val roundedRockPositions = BooleanArray(height * width)
            val cubeShapedRockPositions = BooleanArray(height * width)

            lines.forEachIndexed { y, line ->
                line.forEachIndexed { x, char ->
                    val normalizedPosition = x + y * width

                    when(char) {
                        'O' -> roundedRockPositions[normalizedPosition] = true
                        '#' -> cubeShapedRockPositions[normalizedPosition] = true
                    }
                }
            }

            return ReflectorDish(
                roundedRockPositions = roundedRockPositions,
                cubeShapedRockPositions = cubeShapedRockPositions,
                height = height,
                width = width
            )
        }
    }

    operator fun BooleanArray.get(x: Int, y: Int): Boolean =
        this[x + y * width]

    operator fun BooleanArray.set(x: Int, y: Int, value: Boolean) {
        this[x + y * width] = value
    }
}

fun ReflectorDish.calculateLoad(): Int {
    var totalLoad = 0

    for (y in 0 until height) {
        for (x in 0 until width) {
            if (roundedRockPositions[x, y]) {
                totalLoad += height - y
            }
        }
    }

    return totalLoad
}

private fun ReflectorDish.withUpdatedRoundedRockPositions(updatedRoundedRockPositions: BooleanArray) =
    ReflectorDish(
        roundedRockPositions = updatedRoundedRockPositions,
        cubeShapedRockPositions = cubeShapedRockPositions,
        height = height,
        width = width
    )

fun ReflectorDish.tiltNorth(): ReflectorDish {
    val updatedRoundedRockPositions = BooleanArray(roundedRockPositions.size)

    fun rollRocksTo(x: Int, y: Int, rockCount: Int) {
        repeat(rockCount) { offset ->
            updatedRoundedRockPositions[x, y + offset] = true
        }
    }

    for(x in 0 until width) {
        var index = -1
        var rocks = 0

        for (y in 0 until height) {
            when {
                roundedRockPositions[x, y] -> {
                    rocks += 1
                }
                cubeShapedRockPositions[x, y] -> {
                    rollRocksTo(x, index + 1, rocks)

                    index = y
                    rocks = 0
                }
            }
        }

        rollRocksTo(x, index + 1, rocks)
    }

    return withUpdatedRoundedRockPositions(updatedRoundedRockPositions)
}

fun ReflectorDish.tiltEast(): ReflectorDish {
    val updatedRoundedRockPositions = BooleanArray(roundedRockPositions.size)

    fun rollRocksTo(x: Int, y: Int, rockCount: Int) {
        repeat(rockCount) { offset ->
            updatedRoundedRockPositions[x - offset, y] = true
        }
    }

    for (y in 0 until height) {
        var index = width
        var rocks = 0

        for(x in width - 1 downTo 0) {
            when {
                roundedRockPositions[x, y] -> {
                    rocks += 1
                }
                cubeShapedRockPositions[x, y] -> {
                    rollRocksTo(index - 1, y, rocks)

                    index = x
                    rocks = 0
                }
            }
        }

        rollRocksTo(index - 1, y, rocks)
    }

    return withUpdatedRoundedRockPositions(updatedRoundedRockPositions)
}



fun ReflectorDish.tiltSouth(): ReflectorDish {
    val updatedRoundedRockPositions = BooleanArray(roundedRockPositions.size)

    fun rollRocksTo(x: Int, y: Int, rockCount: Int) {
        repeat(rockCount) { offset ->
            updatedRoundedRockPositions[x, y - offset] = true
        }
    }

    for(x in 0 until width) {
        var index = height
        var rocks = 0

        for (y in height - 1 downTo 0) {
            when {
                roundedRockPositions[x, y] -> {
                    rocks += 1
                }
                cubeShapedRockPositions[x, y] -> {
                    rollRocksTo(x, index - 1, rocks)

                    index = y
                    rocks = 0
                }
            }
        }

        rollRocksTo(x, index - 1, rocks)
    }

    return withUpdatedRoundedRockPositions(updatedRoundedRockPositions)
}

fun ReflectorDish.tiltWest(): ReflectorDish {
    val updatedRoundedRockPositions = BooleanArray(roundedRockPositions.size)

    fun rollRocksTo(x: Int, y: Int, rockCount: Int) {
        repeat(rockCount) { offset ->
            updatedRoundedRockPositions[x + offset, y] = true
        }
    }

    for (y in 0 until height) {
        var index = -1
        var rocks = 0

        for(x in 0 until width) {
            when {
                roundedRockPositions[x, y] -> {
                    rocks += 1
                }
                cubeShapedRockPositions[x, y] -> {
                    rollRocksTo(index + 1, y, rocks)

                    index = x
                    rocks = 0
                }
            }
        }

        rollRocksTo(index + 1, y, rocks)
    }

    return withUpdatedRoundedRockPositions(updatedRoundedRockPositions)
}

private fun ReflectorDish.tiltAround(): ReflectorDish {
    val tiltedNorth = tiltNorth()
    // println(tiltedNorth.toPrintout())
    val tiltedWest = tiltedNorth.tiltWest()
    // println(tiltedWest.toPrintout())
    val tiltedSouth = tiltedWest.tiltSouth()
    // println(tiltedSouth.toPrintout())
    val tiltedEast = tiltedSouth.tiltEast()
    // println(tiltedEast.toPrintout())

    return tiltedEast
}

private fun ReflectorDish.tiltAround(
    cycleCount: Int
): ReflectorDish {
    var reflectorDish1 = this
    val hashSums = mutableListOf<Int>()
    var tilts = 0
    while (tilts < cycleCount) {
        reflectorDish1 = reflectorDish1.tiltAround()
        val hash = reflectorDish1.roundedRockPositions.contentHashCode()

        val firstOccurrence = hashSums.indexOf(hash)
        if (firstOccurrence < 0) {
            hashSums += hash
        } else {
            val repeatingStepWidth = tilts - firstOccurrence

            val foo = (cycleCount - tilts) / repeatingStepWidth
            val bar = tilts + foo * repeatingStepWidth
            tilts = bar
        }

        tilts += 1
    }
    return reflectorDish1
}

fun ReflectorDish.toPrintout(): String {
    return buildString {
        for (y in 0 until height) {
            for (x in 0 until width) {
                append(
                    when {
                        roundedRockPositions[x, y] -> 'O'
                        cubeShapedRockPositions[x, y] -> '#'
                        else -> '.'
                    }
                )
            }
            appendLine()
        }
    }
}

fun part1(input: String): Int {
    val reflectorDish = ReflectorDish.from(input)

    val tiltedReflectorDish = reflectorDish.tiltNorth()
    // println(tiltedReflectorDish.toPrintout())

    return tiltedReflectorDish.calculateLoad()
}

fun part2(input: String): Int {
    val cycleCount = 1_000_000_000

    var reflectorDish = ReflectorDish.from(input)
    reflectorDish = reflectorDish.tiltAround(cycleCount)

    return reflectorDish.calculateLoad()
}

fun main() {
    val testInput = readInput("day14/test-input.txt")
    val input = readInput("day14/input.txt")

    // part 1
    checkSolution(part1(testInput), 136)
    println(part1(input))

    // part 2
     checkSolution(part2(testInput), 64)
     println(part2(input))
}

