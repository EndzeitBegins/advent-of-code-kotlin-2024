package io.github.endzeitbegins.aoc2024.day08

import io.github.endzeitbegins.aoc2024.checkSolution
import io.github.endzeitbegins.aoc2024.readInput

private typealias Frequency = Char

private data class Position(val x: Int, val y: Int)
private data class Antenna(val frequency: Frequency, val position: Position)

private data class CityScan(
    val height: Int,
    val width: Int,
    val antennas: Set<Antenna>,
) {
    operator fun contains(position: Position): Boolean =
        position.x in 0..<width && position.y in 0..<height
}

private fun String.toCityScan(): CityScan {
    val lines = this.lines()

    val height = lines.size
    val width = lines[0].length

    val antennas = mutableSetOf<Antenna>()

    for (x in 0 until width) {
        for (y in 0 until height) {
            val frequency = lines[y][x]

            if (frequency != '.') {
                antennas += Antenna(frequency = frequency, position = Position(x = x, y = y))
            }
        }
    }

    return CityScan(height = height, width = width, antennas = antennas)
}

private val CityScan.antennasByFrequency
    get() = antennas.groupBy { antenna -> antenna.frequency }

private fun CityScan.determineAntinodes(): Map<Frequency, Set<Position>> =
    antennasByFrequency
        .mapValues { (_, antennas) ->
            antennas
                .permutations()
                .flatMap { (antennaA, antennaB) ->
                    setOf(
                        determineAntinode(antennaA.position, antennaB.position),
                        determineAntinode(antennaB.position, antennaA.position),
                    )
                }
                .filter { position -> position in this }
                .toSet()
        }

private fun CityScan.determineResonantAntinodes(): Map<Frequency, Set<Position>> =
    antennasByFrequency
        .mapValues { (_, antennas) ->
            antennas
                .permutations()
                .flatMap { (antennaA, antennaB) ->
                    val antinodesInOneDirection = determineResonantAntinodes(
                        positionA = antennaA.position,
                        positionB = antennaB.position
                    ).takeWhile { position -> position in this }

                    val antinodesInOtherDirection = determineResonantAntinodes(
                        positionA = antennaB.position,
                        positionB = antennaA.position
                    ).takeWhile { position -> position in this }

                    antinodesInOneDirection + antinodesInOtherDirection + antennaA.position + antennaB.position
                }
                .toSet()
        }

private fun <T> List<T>.permutations(): Sequence<Pair<T, T>> = sequence {
    val collection = this@permutations

    for (i in collection.indices) {
        for (j in i + 1..<size) {
            yield(collection[i] to collection[j])
        }
    }
}

private fun determineAntinode(positionA: Position, positionB: Position, level: Int = 1): Position =
    Position(x = positionA.x - level * (positionB.x - positionA.x), y = positionA.y - level * (positionB.y - positionA.y))

private fun determineResonantAntinodes(positionA: Position, positionB: Position): Sequence<Position> = sequence {
    var level = 1

    while (true) {
        yield(determineAntinode(positionA = positionA, positionB = positionB, level = level))

        level += 1
    }
}

private fun part1(input: String): Int {
    val cityScan = input.toCityScan()
    val antinodes = cityScan.determineAntinodes()

    return antinodes
        .flatMap { (_, antinodes) -> antinodes }
        .toSet()
        .size
}

private fun part2(input: String): Int {
    val cityScan = input.toCityScan()
    val resontantAntinodes = cityScan.determineResonantAntinodes()

    return resontantAntinodes
        .flatMap { (_, antinodes) -> antinodes }
        .toSet()
        .size
}

fun main() {
    val testInput = readInput("day08/test-input.txt")
    val input = readInput("day08/input.txt")

    // part 1
    checkSolution(part1(testInput), 14)
    println(part1(input))

    // part 2
    checkSolution(part2(testInput), 34)
    println(part2(input))
}
