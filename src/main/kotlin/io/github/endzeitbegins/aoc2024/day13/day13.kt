package io.github.endzeitbegins.aoc2024.day13

import io.github.endzeitbegins.aoc2024.checkSolution
import io.github.endzeitbegins.aoc2024.readInput

private data class ButtonConfiguration(val xOffset: Long, val yOffset: Long)
private data class Position(val x: Long, val y: Long)
private data class ButtonPresses(val buttonA: Long, val buttonB: Long)

private data class ClawMachine(
    val buttonAConfiguration: ButtonConfiguration,
    val buttonBConfiguration: ButtonConfiguration,
    val prizeLocation: Position
)

private fun solveLinearEquation(a1: Long, b1: Long, result1: Long, a2: Long, b2: Long, result2: Long): Pair<Long, Long>? {
    val determinant = a1 * b2 - a2 * b1

    if (determinant == 0L)
        return null

    val numeratorA = result1 * b2 - result2 * b1
    val numeratorB = a1 * result2 - a2 * result1

    return if (numeratorA % determinant == 0L && numeratorB % determinant == 0L) {
        val a = numeratorA / determinant
        val b = numeratorB / determinant

        a to b
    } else null
}

private fun determineButtonPresses(clawMachine: ClawMachine) = solveLinearEquation(
    a1 = clawMachine.buttonAConfiguration.xOffset,
    b1 = clawMachine.buttonBConfiguration.xOffset,
    result1 = clawMachine.prizeLocation.x,
    a2 = clawMachine.buttonAConfiguration.yOffset,
    b2 = clawMachine.buttonBConfiguration.yOffset,
    result2 = clawMachine.prizeLocation.y,
)?.toButtonPresses()

private fun Pair<Long, Long>.toButtonPresses(): ButtonPresses = ButtonPresses(buttonA = first, buttonB = second)

private fun parseClawMachines(input: String): List<ClawMachine> =
    input.lines()
        .windowed(size = 4, step = 4, partialWindows = true)
        .map { (buttonA, buttonB, prize) ->
            ClawMachine(
                buttonAConfiguration = parseButtonConfiguration(buttonA),
                buttonBConfiguration = parseButtonConfiguration(buttonB),
                prizeLocation = parsePrizeLocation(prize)
            )
        }

private val prizeLocationRegex = """Prize: X=(\d+), Y=(\d+)""".toRegex()

private fun parsePrizeLocation(prizeLocation: String): Position {
    val matchResult = requireNotNull(prizeLocationRegex.matchEntire(prizeLocation))

    return Position(
        x = matchResult.groupValues[1].toLong(),
        y = matchResult.groupValues[2].toLong(),
    )
}

private val buttonConfigurationRegex = """Button \w+: X([+-]\d+), Y([+-]\d+)""".toRegex()

private fun parseButtonConfiguration(buttonConfiguration: String): ButtonConfiguration {
    val matchResult = requireNotNull(buttonConfigurationRegex.matchEntire(buttonConfiguration))

    return ButtonConfiguration(
        xOffset = matchResult.groupValues[1].toLong(),
        yOffset = matchResult.groupValues[2].toLong(),
    )
}

private fun calculateTokensRequired(buttonPresses: ButtonPresses) = buttonPresses.buttonA * 3 + buttonPresses.buttonB

private fun ClawMachine.adjustForUnitConversionError(): ClawMachine {
    return copy(prizeLocation = prizeLocation.copy(x = prizeLocation.x + 10000000000000L, y = prizeLocation.y + 10000000000000))
}

private fun part1(input: String): Long =
    parseClawMachines(input)
        .mapNotNull { clawMachine -> determineButtonPresses(clawMachine) }
        .filter { (buttonAPresses, buttonBPresses) -> buttonAPresses <= 100 && buttonBPresses <= 100 }
        .sumOf(::calculateTokensRequired)

private fun part2(input: String): Long =
    parseClawMachines(input)
        .map(ClawMachine::adjustForUnitConversionError)
        .mapNotNull(::determineButtonPresses)
        .sumOf(::calculateTokensRequired)

fun main() {
    val testInput = readInput("day13/test-input.txt")
    val input = readInput("day13/input.txt")

    // part 1
    checkSolution(part1(testInput), 480)
    println(part1(input))

    // part 2
     println(part2(input))
}
