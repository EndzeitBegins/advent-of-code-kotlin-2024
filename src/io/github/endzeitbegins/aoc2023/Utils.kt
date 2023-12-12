package io.github.endzeitbegins.aoc2023

import kotlin.io.path.Path
import kotlin.io.path.readLines
import kotlin.io.path.readText

fun readInput(name: String) = Path("src/io/github/endzeitbegins/aoc2023/$name").readText()
fun readInputLines(name: String) = readInput(name).lines()

fun <T> checkSolution(solution: T, expectedSolution: T) =
    check(solution == expectedSolution) { "Expected a result of $expectedSolution but got $solution instead" }

fun Int.pow(exp: Int): Long =
    java.math.BigInteger.valueOf(this.toLong()).pow(exp).toLong()

fun gcd(a: Long, b: Long): Long {
    var l = a
    var r = b

    while (r > 0) {
        val temp = r
        r = l % r
        l = temp
    }
    return l
}

fun lcm(a: Long, b: Long): Long {
    return a * (b / gcd(a, b))
}

fun lcm(numbers: Set<Long>): Long {
    var result = numbers.first()

    for (number in numbers) {
        result = lcm(result, number)
    }

    return result
}
