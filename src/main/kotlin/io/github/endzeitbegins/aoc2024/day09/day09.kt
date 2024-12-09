package io.github.endzeitbegins.aoc2024.day09

import io.github.endzeitbegins.aoc2024.checkSolution
import io.github.endzeitbegins.aoc2024.readInput

private fun part1(input: String): Long = input
    .toDiskMap()
    .withBlocksDefragmented()
    .calculateChecksum()

private fun part2(input: String): Long = input
    .toDiskMap()
    .withFilesDefragmented()
    .calculateChecksum()

private typealias DiskMap = List<Section>

sealed interface Section {
    val length: Int
}

data class FileSection(override val length: Int, val fileId: Int) : Section
data class FreeSection(override val length: Int) : Section

private fun String.toDiskMap(): DiskMap {
    var fileId = 0

    return withIndex().map { (index, size) ->
        val sectionLength = size.digitToInt()

        if (index % 2 == 0) {
            FileSection(length = sectionLength, fileId = fileId++)
        } else {
            FreeSection(length = sectionLength)
        }
    }
}

private fun DiskMap.toPrintout(): String = buildString {
    for (section in this@toPrintout) {
        val char = if (section is FileSection) section.fileId else '.'

        repeat(section.length) {
            append(char)
        }
    }
}

private fun DiskMap.withBlocksDefragmented(): DiskMap {
    val diskMap = this.toMutableList()

    var reverseIndex = 0
    while (reverseIndex < diskMap.size) {
        val section = diskMap[diskMap.lastIndex - reverseIndex]

        if (section is FileSection) {
            val firstFreeSectionIndex = diskMap.indexOfFirst { searchSection ->
                searchSection is FreeSection
            }

            if (firstFreeSectionIndex > 0 && firstFreeSectionIndex < diskMap.size - reverseIndex) {
                val freeSpace = diskMap[firstFreeSectionIndex].length

                if (freeSpace > section.length) {
                    diskMap[firstFreeSectionIndex] = FileSection(length = section.length, fileId = section.fileId)
                    diskMap[diskMap.lastIndex - reverseIndex] = FreeSection(length = section.length)

                    diskMap.add(firstFreeSectionIndex + 1, FreeSection(length = freeSpace - section.length))
                } else if (freeSpace < section.length) {
                    diskMap[firstFreeSectionIndex] = FileSection(length = freeSpace, fileId = section.fileId)
                    diskMap[diskMap.lastIndex - reverseIndex] = FreeSection(length = freeSpace)
                    diskMap.add(
                        diskMap.lastIndex - reverseIndex,
                        FileSection(length = section.length - freeSpace, fileId = section.fileId)
                    )
                } else {
                    diskMap[firstFreeSectionIndex] = FileSection(length = section.length, fileId = section.fileId)
                    diskMap[diskMap.lastIndex - reverseIndex] = FreeSection(length = section.length)
                }
            }

            reverseIndex += 1
        } else {
            reverseIndex += 1
        }
    }

    return diskMap
}

private fun DiskMap.withFilesDefragmented(): DiskMap {
    val diskMap = this.toMutableList()

    var reverseIndex = 0
    while (reverseIndex < diskMap.size) {
        val section = diskMap[diskMap.lastIndex - reverseIndex]

        if (section is FileSection) {
            val firstFreeSectionIndex = diskMap.indexOfFirst { searchSection ->
                searchSection is FreeSection && searchSection.length >= section.length
            }

            if (firstFreeSectionIndex > 0 && firstFreeSectionIndex < diskMap.size - reverseIndex) {
                val freeSpace = diskMap[firstFreeSectionIndex].length
                val remainingFreeSpace = freeSpace - section.length

                diskMap[firstFreeSectionIndex] = section
                diskMap[diskMap.lastIndex - reverseIndex] = FreeSection(length = section.length)

                if (remainingFreeSpace > 0) {
                    diskMap.add(firstFreeSectionIndex + 1, FreeSection(length = remainingFreeSpace))
                }
            }

            reverseIndex += 1
        } else {
            reverseIndex += 1
        }
    }

    return diskMap
}

private fun DiskMap.calculateChecksum(): Long {
    var checksum = 0L
    var index = 0

    for (section in this) {
        if (section is FileSection) {
            repeat(section.length) {
                checksum += index * section.fileId
                index += 1
            }
        } else {
            index += section.length
        }
    }

    return checksum
}

fun main() {
    val testInput = readInput("day09/test-input.txt")
    val input = readInput("day09/input.txt")

    // part 1
    checkSolution(part1(testInput), 1928)
    println(part1(input))

    // part 2
    checkSolution(part2(testInput), 2858)
    println(part2(input))
}
