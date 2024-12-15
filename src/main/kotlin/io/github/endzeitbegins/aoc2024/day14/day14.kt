package io.github.endzeitbegins.aoc2024.day14

import io.github.endzeitbegins.aoc2024.checkSolution
import io.github.endzeitbegins.aoc2024.readInput

private data class Position(val x: Int, val y: Int)
private data class RobotConfiguration(val startPosition: Position, val velocity: Position)
private data class Robot(val configuration: RobotConfiguration, val position: Position)

private fun Robot.simulateMovement(seconds: Long, width: Int, height: Int): Robot {
    var updatedX = configuration.startPosition.x + seconds * configuration.velocity.x
    var updatedY = configuration.startPosition.y + seconds * configuration.velocity.y

    while (updatedX < 0) {
        updatedX += width
    }
    while (updatedY < 0) {
        updatedY += height
    }

    return copy(position = Position(x = updatedX.toInt() % width, y = updatedY.toInt() % height))
}

private val robotConfigurationRegex = """p=(\d+),(\d+) v=(-?\d+),(-?\d+)""".toRegex()

private fun parseRobotConfigurations(input: String): List<RobotConfiguration> =
    input
        .lines()
        .map { line ->
            val matchResult = requireNotNull(robotConfigurationRegex.matchEntire(line))

            RobotConfiguration(
                startPosition = Position(
                    x = matchResult.groupValues[1].toInt(),
                    y = matchResult.groupValues[2].toInt()
                ),
                velocity = Position(x = matchResult.groupValues[3].toInt(), y = matchResult.groupValues[4].toInt())
            )
        }

private data class Quadrant(val xRange: IntRange, val yRange: IntRange) {
    operator fun contains(position: Position): Boolean =
        position.x in xRange && position.y in yRange
}

private data class Quadrants(
    val quadrantA: List<Robot>,
    val quadrantB: List<Robot>,
    val quadrantC: List<Robot>,
    val quadrantD: List<Robot>,
)

private fun List<Robot>.groupByQuadrant(width: Int, height: Int): Quadrants {
    val quadrantA = Quadrant(xRange = 0..<width / 2, yRange = 0..<height / 2)
    val quadrantB = Quadrant(xRange = width / 2 + 1..<width, yRange = 0..<height / 2)
    val quadrantC = Quadrant(xRange = 0..<width / 2, yRange = height / 2 + 1..<height)
    val quadrantD = Quadrant(xRange = width / 2 + 1..<width, yRange = height / 2 + 1..<height)

    val quadrantARobots = mutableListOf<Robot>()
    val quadrantBRobots = mutableListOf<Robot>()
    val quadrantCRobots = mutableListOf<Robot>()
    val quadrantDRobots = mutableListOf<Robot>()

    for (robot in this) {
        when (robot.position) {
            in quadrantA -> quadrantARobots += robot
            in quadrantB -> quadrantBRobots += robot
            in quadrantC -> quadrantCRobots += robot
            in quadrantD -> quadrantDRobots += robot
        }
    }

    return Quadrants(
        quadrantA = quadrantARobots,
        quadrantB = quadrantBRobots,
        quadrantC = quadrantCRobots,
        quadrantD = quadrantDRobots
    )
}

private fun RobotConfiguration.initializeRobot(): Robot = Robot(configuration = this, position = startPosition)

private fun part1(input: String, width: Int, height: Int): Int {
    val robotConfigurations = parseRobotConfigurations(input)
    val robots = robotConfigurations.map(RobotConfiguration::initializeRobot)

    val simulatedRobots = robots.map { robot -> robot.simulateMovement(seconds = 100, width = width, height = height) }
    val quadrants = simulatedRobots.groupByQuadrant(width, height)

    return quadrants.quadrantA.size * quadrants.quadrantB.size * quadrants.quadrantC.size * quadrants.quadrantD.size
}

private fun generateMap(simulatedRobots: List<Robot>, width: Int, height: Int): String = buildString {
    for (y in 0 until height) {
        for (x in 0 until width) {
            val robotCount = simulatedRobots.count { robot -> robot.position.x == x && robot.position.y == y }

            append(if (robotCount >= 1) "$robotCount".take(1) else ".")
        }
        appendLine()
    }
}

private fun part2(input: String, width: Int, height: Int) {
    val robotConfigurations = parseRobotConfigurations(input)
    val robots = robotConfigurations.map(RobotConfiguration::initializeRobot)

    repeat(10_000) { index ->
        val seconds = 1L * index
        val simulatedRobots = robots
            .map { robot -> robot.simulateMovement(seconds = seconds, width = width, height = height) }

        val x = simulatedRobots
            .groupBy { it.position.y }
            .map { (_, robotsInRow) -> robotsInRow.map(Robot::position).sortedBy(Position::x) }
            .filter { xCoordinates -> xCoordinates.size > width / 2 }
            .any()
            // .any { xCoordinates -> }

        if (x) {
            val map = generateMap(simulatedRobots, width = width, height = height)

            println("""
            |Iteration $seconds
            |$map
            |
        """.trimMargin())
        }
    }
}

fun main() {
    val testInput = readInput("day14/test-input.txt")
    val input = readInput("day14/input.txt")

    // part 1
    checkSolution(part1(testInput, width = 11, height = 7), 12)
    println(part1(input, width = 101, height = 103))

    // part 2
    part2(input, width = 101, height = 103)
}
