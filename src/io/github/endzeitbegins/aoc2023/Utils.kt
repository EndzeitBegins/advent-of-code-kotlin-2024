package io.github.endzeitbegins.aoc2023

import kotlin.io.path.Path
import kotlin.io.path.readLines
import kotlin.io.path.readText

fun readInput(name: String) = Path("src/io/github/endzeitbegins/aoc2023/$name").readText()
fun readInputLines(name: String) = readInput(name).lines()

fun <T> checkSolution(solution: T, expectedSolution: T) =
    check(solution == expectedSolution) { "Expected a result of $expectedSolution but got $solution instead" }