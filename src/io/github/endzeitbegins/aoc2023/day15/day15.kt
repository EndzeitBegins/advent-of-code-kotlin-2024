package io.github.endzeitbegins.aoc2023.day15

import io.github.endzeitbegins.aoc2023.checkSolution
import io.github.endzeitbegins.aoc2023.readInput

fun String.calculateHash(): Int {
    var hashValue = 0

    for (char in this) {
        val asciiValue = char.code

        hashValue += asciiValue
        hashValue *= 17
        hashValue %= 256
    }

    return hashValue
}

data class Lens(
    val label: String,
    val focalLength: Int,
)

fun part1(input: String): Int {
    val steps = input.split(',')

    return steps
        .sumOf { step -> step.calculateHash() }
}

fun part2(input: String): Int {
    val steps = input.split(',')

    val boxes = List(256) {
        mutableListOf<Lens>()
    }
    for (step in steps) {
        val parts = step.split('-', '=')

        val label = parts[0]
        val boxIndex = label.calculateHash()

        val action = step[label.length]
        if (action == '=') {
            val focalLength = step.substring(label.length + 1).toInt()
            val newLens = Lens(label = label, focalLength = focalLength)

            val indexOfLens = boxes[boxIndex].indexOfFirst { lens -> lens.label == label }
            if (indexOfLens >= 0) {
                boxes[boxIndex][indexOfLens] = newLens
            } else {
                boxes[boxIndex].add(newLens)
            }
        } else {
            boxes[boxIndex].removeIf { lens -> lens.label == label }
        }
    }

    return boxes.foldIndexed(0) { boxNumber, focusingPower, box ->
        focusingPower + box.foldIndexed(0) { slot, boxFocusingPower, lens ->
            boxFocusingPower + (boxNumber + 1) * (slot + 1) * lens.focalLength
        }
    }
}

fun main() {
    val testInput = readInput("day15/test-input.txt")
    val testInput2 = readInput("day15/test-input2.txt")
    val input = readInput("day15/input.txt")

    // part 1
    checkSolution(part1(testInput), 52)
    checkSolution(part1(testInput2), 1320)
    println(part1(input))

    // part 2
     checkSolution(part2(testInput2), 145)
     println(part2(input))
}
