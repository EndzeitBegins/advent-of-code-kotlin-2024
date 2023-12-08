package io.github.endzeitbegins.aoc2023.day06

import io.github.endzeitbegins.aoc2023.checkSolution
import io.github.endzeitbegins.aoc2023.readInput

data class Highscore(
    val duration: Long,
    val distance: Long,
)

fun parseMultiRaceInput(input: String): List<Highscore> {
    val lines = input.lines()

    val times = lines
        .first()
        .split("""\s+""".toRegex())
        .drop(1)
        .map(String::toLong)

    val distances = lines
        .last()
        .split("""\s+""".toRegex())
        .drop(1)
        .map(String::toLong)

    return times.zip(distances)
        .map { (duration, distance) -> Highscore(duration, distance) }
}

fun parseSingleRaceInput(input: String): Highscore {
    return input
        .lines()
        .windowed(2)
        .map { (timeLine, distanceLine) ->
            Highscore(
                duration = timeLine
                    .removePrefix("Time:")
                    .replace("""\s+""".toRegex(), "")
                    .toLong(),
                distance = distanceLine
                    .removePrefix("Distance:")
                    .replace("""\s+""".toRegex(), "")
                    .toLong(),
            )
        }
        .single()
}

fun determineDistance(chargeTime: Long, travelTime: Long): Long {
    val speed = chargeTime

    return 1L * travelTime * speed
}

fun Highscore.findMinimumWinningChargeTime(): Long {
    for (chargeTime in 0..duration) {
        val achievedDistance = determineDistance(chargeTime, duration - chargeTime)
        if (achievedDistance > distance) {
            return chargeTime
        }
    }
    return duration
}

fun Highscore.findMaximumWinningChargeTime(): Long {
    for (chargeTime in duration.downTo(0)) {
        val achievedDistance = determineDistance(chargeTime, duration - chargeTime)
        if (achievedDistance > distance) {
            return chargeTime
        }
    }
    return 0
}

fun Highscore.countWinningStrategies(): Long {
    val min = findMinimumWinningChargeTime()
    val max = findMaximumWinningChargeTime()

    return max - min + 1
}

fun part1(input: String): Long {
    val highscores = parseMultiRaceInput(input)
    val winningStrategyCounts = highscores
        .map { it.countWinningStrategies() }

    return winningStrategyCounts
        .fold(1L) { aggregate, count -> aggregate * count }
}

fun part2(input: String): Long {
    val highscore = parseSingleRaceInput(input)

    return highscore.countWinningStrategies()
}

fun main() {
    val testInput = readInput("day06/test-input.txt")
    val input = readInput("day06/input.txt")

    // part 1
    checkSolution(part1(testInput), 288)
    println(part1(input))

    // part 2
     checkSolution(part2(testInput), 71503)
     println(part2(input))
}
