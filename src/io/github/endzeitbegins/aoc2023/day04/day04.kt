package io.github.endzeitbegins.aoc2023.day04

import io.github.endzeitbegins.aoc2023.checkSolution
import io.github.endzeitbegins.aoc2023.pow
import io.github.endzeitbegins.aoc2023.readInput

data class ScratchCard(
    val id: Int,
    val winningNumbers: Set<Int>,
    val playerNumbers: Set<Int>,
) {
    private val intersectingNumbers = playerNumbers.intersect(winningNumbers)

    val score = if (intersectingNumbers.isEmpty()) 0 else 2.pow(intersectingNumbers.size - 1)
    val winnedCopies =
        if (intersectingNumbers.isEmpty()) emptySet() else (id + 1..id + intersectingNumbers.size).toSet()
}

private fun parseScratchCards(input: String): List<ScratchCard> {
    return input
        .lines()
        .map { line ->
            val (header, winningPart, playerPart) = line.split(":", "|")
            val id = header.drop(5).trim().toInt()

            val winningNumbers = winningPart
                .split(" ")
                .filterNot(String::isBlank)
                .map(String::toInt)
                .toSet()
            val playerNumbers = playerPart
                .split(" ")
                .filterNot(String::isBlank)
                .map(String::toInt)
                .toSet()

            ScratchCard(id, winningNumbers, playerNumbers)
        }
}

fun part1(input: String): Long {
    val cards = parseScratchCards(input)

    return cards.sumOf { card -> card.score }
}

fun part2(input: String): Int {
    val cards = parseScratchCards(input)

    val cardCount = cards
        .associate { it.id to 1 }
        .toMutableMap()
    for(card in cards) {
        val count = cardCount.getValue(card.id)

        for(winnedCopy in card.winnedCopies) {
            cardCount[winnedCopy] = cardCount.getValue(winnedCopy) + count
        }
    }

    return cardCount.values.sum()
}

fun main() {
    val testInput = readInput("day04/test-input.txt")
    val input = readInput("day04/input.txt")

    // part 1
    checkSolution(part1(testInput), 13)
    println(part1(input))

    // part 2
     checkSolution(part2(testInput), 30)
     println(part2(input))
}
