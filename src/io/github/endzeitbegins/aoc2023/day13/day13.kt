package io.github.endzeitbegins.aoc2023.day13

import io.github.endzeitbegins.aoc2023.checkSolution
import io.github.endzeitbegins.aoc2023.readInput

data class Pattern(
    val rows: List<String>,
    val columns: List<String>,
)

fun List<String>.determinePossibleReflectionPoints(needsSmudge: Boolean): List<Int> {
    val allowedSmudges = if (needsSmudge) 1 else 0

    return zipWithNext()
        .withIndex()
        .filter { (_, pair) ->
            pair.first.equalsWithSmudges(pair.second, allowedSmudges)
        }
        .map { (index, _) -> index }
}

fun String.equalsWithSmudges(other: String, allowedSmudges: Int): Boolean {
    var smudges = 0

    for ((c1, c2) in this.zip(other)) {
        if (c1 != c2) {
            smudges += 1

            if (smudges > allowedSmudges) {
                return false
            }
        }
    }

    return true
}

fun List<String>.reflectsAt(index: Int, needsSmudge: Boolean): Boolean {
    var requiredSmudges = if (needsSmudge) 1 else 0

    var i = index
    var j = index + 1

    while (i >= 0 && j <= lastIndex) {
        val a = this[i]
        val b = this[j]

        if (a != b) {
            if (requiredSmudges >= 1 && a.equalsWithSmudges(b, requiredSmudges)) {
                requiredSmudges = 0
            } else {
                return false
            }
        }

        i -= 1
        j += 1
    }

    return requiredSmudges == 0
}

fun Pattern.determineReflectionPoints(needsSmudge: Boolean = false): ReflectionPoint {
    return determineReflectingColumns(needsSmudge).takeIf { it.isNotEmpty() }?.single()
        ?: determineReflectingRows(needsSmudge).single()
}

private fun Pattern.determineReflectingColumns(needsSmudge: Boolean): List<ReflectionPoint.Column> {
    return columns
        .determinePossibleReflectionPoints(needsSmudge = needsSmudge)
        .filter { index -> columns.reflectsAt(index, needsSmudge) }
        .map { index -> ReflectionPoint.Column(index) }
}

private fun Pattern.determineReflectingRows(needsSmudge: Boolean): List<ReflectionPoint.Row> {
    return rows
        .determinePossibleReflectionPoints(needsSmudge = needsSmudge)
        .filter { index -> rows.reflectsAt(index, needsSmudge) }
        .map { index -> ReflectionPoint.Row(index) }
}

sealed interface ReflectionPoint {

    val index: Int

    data class Row(override val index: Int) : ReflectionPoint
    data class Column(override val index: Int) : ReflectionPoint
}

val ReflectionPoint.score: Int
    get() = when (this) {
        is ReflectionPoint.Column -> index + 1
        is ReflectionPoint.Row -> (index + 1) * 100
    }


fun parsePatterns(input: String): List<Pattern> {
    val rows = mutableListOf<String>()
    val patterns = mutableListOf<Pattern>()

    for (line in input.lines()) {
        if (line.isBlank()) {
            patterns.add(rowsToPattern(rows.toList()))

            rows.clear()
        } else {
            rows += line
        }
    }
    patterns.add(rowsToPattern(rows))

    return patterns
}

private fun rowsToPattern(rows: List<String>): Pattern {
    val columns = List(rows.first().length) { index ->
        rows.map { it[index] }.joinToString(separator = "")
    }

    return Pattern(rows = rows, columns = columns)
}

fun part1(input: String): Int {
    val patterns = parsePatterns(input)

    return patterns
        .sumOf { pattern -> pattern.determineReflectionPoints().score }
}

fun part2(input: String): Int {
    val patterns = parsePatterns(input)

    return patterns
        .sumOf { pattern ->
            val reflectionWithSmudge = pattern
                .determineReflectionPoints(needsSmudge = true)

            reflectionWithSmudge.score
        }
}

fun main() {
    val testInput = readInput("day13/test-input.txt")
    val input = readInput("day13/input.txt")

    // part 1
    checkSolution(part1(testInput), 405)
    println(part1(input))

    // part 2
    checkSolution(part2(testInput), 400)
    println(part2(input))
}
