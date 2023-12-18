package io.github.endzeitbegins.aoc2023.day18

import io.github.endzeitbegins.aoc2023.checkSolution
import io.github.endzeitbegins.aoc2023.readInput
import kotlin.math.absoluteValue

enum class Direction(val sign: Char) {
    UP('U'),
    DOWN('D'),
    LEFT('L'),
    RIGHT('R');

    companion object {
        fun fromSign(sign: Char): Direction =
            entries.single { it.sign == sign }
    }
}

data class Movement(
    val direction: Direction,
    val steps: Int,
    val color: String,
)

typealias DigPlan = List<Movement>

fun parseDigPlan(input: String): DigPlan =
    input
        .lines()
        .map { line ->
            val (directionSign, steps, color) = line.split(' ', limit = 3)

            Movement(
                direction = Direction.fromSign(directionSign.single()),
                steps = steps.toInt(),
                color = color.removeSurrounding("(", ")")
            )
        }


data class TrenchPosition(
    val x: Int,
    val y: Int,
    val color: String,
)

fun DigPlan.calculateTrenchPositions(): List<TrenchPosition> {
    val trench = mutableListOf<TrenchPosition>()

    var x = 0
    var y = 0
    trench += TrenchPosition(x = x, y = y, color = "#000000")

    for (movement in this) {
        repeat(movement.steps) {
            when (movement.direction) {
                Direction.UP -> y -= 1
                Direction.DOWN -> y += 1
                Direction.LEFT -> x -= 1
                Direction.RIGHT -> x += 1
            }

            trench += TrenchPosition(x = x, y = y, color = movement.color)
        }
    }

    val xOffset = trench.minOf { it.x }.takeIf { it < 0 }?.absoluteValue ?: 0
    val yOffset = trench.minOf { it.y }.takeIf { it < 0 }?.absoluteValue ?: 0

    return trench.map { trenchPosition ->
        trenchPosition.copy(
            x = trenchPosition.x + xOffset + 1,
            y = trenchPosition.y + yOffset + 1,
        )
    }
}

class TrenchPlan(val trenchPositions: List<List<String?>>) {
    val height = trenchPositions.size
    val width = trenchPositions.first().size
}

operator fun TrenchPlan.get(x: Int, y: Int): String? =
    trenchPositions[y][x]

fun List<TrenchPosition>.toTrenchPlan(): TrenchPlan {
    operator fun MutableList<MutableList<String?>>.set(x: Int, y: Int, color: String) {
        this[y][x] = color
    }

    val width = maxOf { it.x } + 2
    val height = maxOf { it.y } + 2

    val trenchPlan: MutableList<MutableList<String?>> = MutableList(height) {
        MutableList(width) { null }
    }

    for (trenchPosition in this) {
        trenchPlan[trenchPosition.x, trenchPosition.y] = trenchPosition.color
    }

    return TrenchPlan(trenchPlan)
}

data class ParsedColor(val red: Int, val green: Int, val blue: Int)

fun parseColor(color: String): ParsedColor =
    ParsedColor(
        red = color.substring(1, 3).toInt(16),
        green = color.substring(3, 5).toInt(16),
        blue = color.substring(5, 7).toInt(16),
    )

fun TrenchPlan.countGroundOutside(): Int {
    val outsidePositions = mutableSetOf<Pair<Int, Int>>()
    val positionsToCheck = mutableListOf(
        0 to 0
    )

    while (positionsToCheck.isNotEmpty()) {
        val positionToCheck = positionsToCheck.removeFirst()
        val (x, y) = positionToCheck

        if (get(x, y) == null) {
            outsidePositions += positionToCheck

            val neighbours = listOf(
                (x + 1) to y,
                (x - 1) to y,
                x to (y + 1),
                x to (y - 1),
            )

            positionsToCheck += neighbours
                .filterNot { it in positionsToCheck }
                .filterNot { it in outsidePositions }
                .filter { (x, _) -> x in 0 until width }
                .filter { (_, y) -> y in 0 until height }
        }
    }

    return outsidePositions.size
}

fun TrenchPlan.toPrintout(): String {
    val sb = StringBuilder()

    for (y in 0 until height) {
        for (x in 0 until width) {
            val trenchColor = get(x, y)

            if (trenchColor == null) {
                sb.append('.')
            } else {
                val (red, green, blue) = parseColor(trenchColor)

                sb.append("\u001B[38;2;${red};${green};${blue}m#\u001B[0m")
            }
        }
        sb.appendLine()
    }

    return sb.toString()
}

fun part1(input: String): Int {
    val trenchPlan = parseDigPlan(input)
        .calculateTrenchPositions()
        .toTrenchPlan()

    println(trenchPlan.toPrintout())

    val totalSize = trenchPlan.height * trenchPlan.width
    val outside = trenchPlan.countGroundOutside()
    val trenchSize = trenchPlan.trenchPositions.size
    val inside = totalSize - outside - trenchSize

    return trenchSize + inside
}

fun part2(input: String): Long {
    return input.length.toLong()
}

fun main() {
    val testInput = readInput("day18/test-input.txt")
    val input = readInput("day18/input.txt")

    // part 1
    checkSolution(part1(testInput), 62)
    println(part1(input))

    // part 2
     checkSolution(part2(testInput), 952408144115)
    // println(part2(input))
}
