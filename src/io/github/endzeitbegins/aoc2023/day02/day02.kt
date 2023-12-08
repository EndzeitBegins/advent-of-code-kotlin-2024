package io.github.endzeitbegins.aoc2023.day02

import io.github.endzeitbegins.aoc2023.checkSolution
import io.github.endzeitbegins.aoc2023.readInput

enum class CubeColor {
    RED, GREEN, BLUE
}

data class Game(
    val id: Int,
    val revealedColors: List<Pair<CubeColor, Int>>,
) {
    val maxRed: Int = revealedColors.maxOf(CubeColor.RED)
    val maxGreen: Int = revealedColors.maxOf(CubeColor.GREEN)
    val maxBlue: Int = revealedColors.maxOf(CubeColor.BLUE)

    private fun List<Pair<CubeColor, Int>>.maxOf(cubeColor: CubeColor): Int =
        this
            .filter { (color, _) -> color == cubeColor }
            .maxOfOrNull { (_, amount) -> amount } ?: 0

}

val Game.power: Int
    get() = maxRed * maxGreen * maxBlue

private fun parseGames(input: String) = input
    .lines()
    .map { line ->
        val (header, reveals) = line.split(":", limit = 2)
        val gameId = header.drop(5).toInt()
        val revealedColors = reveals
            .split(",", ";")
            .map { revealedCubes ->
                val (amount, rawColor) = revealedCubes
                    .trim()
                    .split(" ", limit = 2)

                val color = when (rawColor) {
                    "red" -> CubeColor.RED
                    "green" -> CubeColor.GREEN
                    else -> CubeColor.BLUE
                }

                color to amount.toInt()
            }

        Game(gameId, revealedColors)
    }

fun part1(input: String): Int {
    val games = parseGames(input)

    val possibleGames = games.filter { game ->
        game.maxRed <= 12 && game.maxGreen <= 13 && game.maxBlue <= 14
    }

    return possibleGames.sumOf { it.id }
}

fun part2(input: String): Int {
    val games = parseGames(input)

    return games.sumOf { it.power }
}

fun main() {
    val testInput = readInput("day02/test-input.txt")
    val input = readInput("day02/input.txt")

    // part 1
    checkSolution(part1(testInput), 8)
    println(part1(input))

    // part 2
     checkSolution(part2(testInput), 2286)
     println(part2(input))
}
