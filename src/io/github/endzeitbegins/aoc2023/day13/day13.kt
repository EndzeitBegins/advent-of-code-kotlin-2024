package io.github.endzeitbegins.aoc2023.day13

import io.github.endzeitbegins.aoc2023.checkSolution
import io.github.endzeitbegins.aoc2023.readInput

data class Pattern(
    val rows: List<String>,
    val columns: List<String>,
)

fun List<String>.determinePossibleReflectionPoints(supportSmudge: Boolean): List<Int> {
    val allowedSmudges = if (supportSmudge) 1 else 0

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

fun List<String>.reflectsAt(index: Int, supportSmudge: Boolean): Boolean {
    var allowedSmudges = if (supportSmudge) 1 else 0

    var i = index
    var j = index + 1

    while (i >= 0 && j <= lastIndex) {
        val a = this[i]
        val b = this[j]

        if (a != b) {
            if (allowedSmudges >= 1 && a.equalsWithSmudges(b, allowedSmudges)) {
                allowedSmudges -= 1
            } else {
                return false
            }
        }

        i -= 1
        j += 1
    }

    return true
}

fun Pattern.determineReflectionPoints(supportSmudge: Boolean = false): List<ReflectionPoint> {
    val reflectingColumns = determineReflectingColumns(supportSmudge)
    val reflectingRows = determineReflectingRows(supportSmudge)

    return reflectingColumns + reflectingRows
}

private fun Pattern.determineReflectingColumns(supportSmudge: Boolean): List<ReflectionPoint.Column> {
    return columns
        .determinePossibleReflectionPoints(supportSmudge = supportSmudge)
        .filter { index -> columns.reflectsAt(index, supportSmudge) }
        .map { index -> ReflectionPoint.Column(index) }
}

private fun Pattern.determineReflectingRows(supportSmudge: Boolean): List<ReflectionPoint.Row> {
    return rows
        .determinePossibleReflectionPoints(supportSmudge = supportSmudge)
        .filter { index -> rows.reflectsAt(index, supportSmudge) }
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
        .flatMap { pattern -> pattern.determineReflectionPoints() }
        .sumOf { reflectionPoint -> reflectionPoint.score }
}

fun part2(input: String): Int {
    val patterns = parsePatterns(input)

    return patterns
        .map { pattern ->
            val reflectionsWithoutSmudge = pattern
                .determineReflectionPoints(supportSmudge = false)
            val reflectionsWithSmudge = pattern
                .determineReflectionPoints(supportSmudge = true)

            val smudgeReflection = (reflectionsWithSmudge - reflectionsWithoutSmudge)
                .minBy { it.index }

            val index = smudgeReflection.index
            val updatedPatternRows: List<String> = when(smudgeReflection) {
                is ReflectionPoint.Column -> pattern.columns.fixSmudge(index).columnsToRows()
                is ReflectionPoint.Row -> { pattern.rows.fixSmudge(index) }
            }

            val updatedPattern: Pattern =
                rowsToPattern(updatedPatternRows)

            val xx = updatedPattern.determineReflectionPoints(supportSmudge = false)
            xx
                .minBy { it.index }

        }
        .sumOf { reflectionPoint -> reflectionPoint.score }
}

private fun List<String>.columnsToRows(): List<String> {
    return List(this.first().length) { index ->
        this.map { it[index] }.joinToString(separator = "")
    }
}

private fun List<String>.fixSmudge(index: Int): List<String> {
    val updated = this.toMutableList()

    var i = index
    var j = index + 1

    while (i >= 0 && j <= lastIndex) {
        val a = this[i]
        val b = this[j]

        if (a != b) {
            updated[i] = updated[j]

            return updated
        }

        i -= 1
        j += 1
    }

    return updated
//    throw IllegalStateException("No smudge found!")
}

fun main() {
    val testInput = readInput("day13/test-input.txt")
    val input = readInput("day13/input.txt")

    // part 1
    checkSolution(part1(testInput), 405)
    println(part1(input))

    // part 2
    checkSolution(part2(testInput), 400)
//    println(part2(input))
}
