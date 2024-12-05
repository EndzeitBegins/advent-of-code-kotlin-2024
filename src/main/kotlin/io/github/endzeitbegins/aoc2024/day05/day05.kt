package io.github.endzeitbegins.aoc2024.day05

import io.github.endzeitbegins.aoc2024.checkSolution
import io.github.endzeitbegins.aoc2024.readInput

private typealias Rules = Map<Int, List<Int>>
private typealias Update = List<Int>
private typealias Updates = List<Update>

private fun part1(input: String): Int {
    val (rules: Rules, updates: Updates) = parseInput(input)

    return updates
        .filter { update -> update.isCorrectlyOrdered(rules) }
        .sumOf(Update::middlePage)
}

private fun part2(input: String): Int {
    val (rules: Rules, updates: Updates) = parseInput(input)

    return updates
        .filterNot { update -> update.isCorrectlyOrdered(rules) }
        .map { update -> update.order(rules) }
        .sumOf(Update::middlePage)
}


private fun parseInput(input: String): Pair<Rules, Updates> {
    val (rulesSection, updatesSection) = input.split("\n\n", limit = 2)

    val rules: Rules = rulesSection
        .lineSequence()
        .map { line -> line.split("|", limit = 2) }
        .groupBy({ (page, _) -> page.toInt() }, { (_, followingPage) -> followingPage.toInt() })

    val updates: Updates = updatesSection
        .lineSequence()
        .map { line -> line.split(",").map(String::toInt) }
        .toList()

    return rules to updates
}

private fun Update.isCorrectlyOrdered(rules: Rules): Boolean {
    val update: Update = this

    for ((index, pageUpdate) in update.withIndex()) {
        val followingPages = rules[pageUpdate]
            ?: continue

        val hasPageInWrongOrder = update
            .subList(fromIndex = 0, toIndex = index)
            .any { pageBefore -> pageBefore in followingPages }

        if (hasPageInWrongOrder)
            return false
    }

    return true
}

private fun Update.order(rules: Rules): Update =
    this.sortedWith { lhs, rhs ->
        when {
            rhs in (rules[lhs] ?: emptyList()) -> -1
            lhs in (rules[rhs] ?: emptyList()) -> 1
            else -> 0
        }
    }

private val Update.middlePage
    get() = this[size / 2]


fun main() {
    val testInput = readInput("day05/test-input.txt")
    val input = readInput("day05/input.txt")

    // part 1
    checkSolution(part1(testInput), 143)
    println(part1(input))

    // part 2
     checkSolution(part2(testInput), 123)
     println(part2(input))
}
