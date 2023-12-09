package io.github.endzeitbegins.aoc2023.day05

import io.github.endzeitbegins.aoc2023.checkSolution
import io.github.endzeitbegins.aoc2023.readInput

data class Mapping(
    val destinationRangeStart: Long,
    val sourceRangeStart: Long,
    val rangeLength: Long,
) {
    val sourceRange = sourceRangeStart until sourceRangeStart + rangeLength
    val destinationRange = destinationRangeStart until destinationRangeStart + rangeLength

    fun isMapped(source: Long) = source in sourceRange
    fun map(source: Long): Long {
        return if (isMapped(source)) {
            val offset = source - sourceRangeStart

            destinationRangeStart + offset
        } else error("Attempted to map unmapped source value $source")
    }
}

data class MappingTable(
    val rows: List<Mapping>,
) {
    fun map(source: Long): Long {
        return rows.find { it.isMapped(source) }
            ?.map(source) ?: source
    }
}

data class Almanac(
    val seeds: Set<Long>,
    val seedRanges: List<LongRange>,
    val seedToSoil: MappingTable,
    val soilToFertilizer: MappingTable,
    val fertilizerToWater: MappingTable,
    val waterToLight: MappingTable,
    val lightToTemperature: MappingTable,
    val temperatureToHumidity: MappingTable,
    val humidityToLocation: MappingTable,
)

private fun parseAlmanac(input: String): Almanac {
    val lines = input.lines().toMutableList()

    val seeds = mutableListOf<Long>()
    val seedRanges = mutableListOf<LongRange>()
    val seedToSoil = mutableListOf<Mapping>()
    val soilToFertilizer = mutableListOf<Mapping>()
    val fertilizerToWater = mutableListOf<Mapping>()
    val waterToLight = mutableListOf<Mapping>()
    val lightToTemperature = mutableListOf<Mapping>()
    val temperatureToHumidity = mutableListOf<Mapping>()
    val humidityToLocation = mutableListOf<Mapping>()

    var activeMapping: MutableList<Mapping> = seedToSoil
    for (line in lines) {
        when {
            line.startsWith("seeds:") -> {
                val numbers = line
                    .removePrefix("seeds: ")
                    .split(" ")
                    .map(String::toLong)

                seeds.addAll(numbers)
                seedRanges.addAll(
                    numbers
                        .windowed(size = 2, step = 2)
                        .map { (from, length) -> from..<from + length }
                        .sortedBy { it.first }
                        .fold(listOf()) { ranges, range ->
                            if (ranges.isEmpty() || range.first > ranges.last().last) {
                                ranges.plusElement(range)
                            } else {
                                ranges.dropLast(1).plusElement(ranges.last().first..range.last)
                            }
                        }
                )
            }

            line == "seed-to-soil map:" -> {
                activeMapping = seedToSoil
            }

            line == "soil-to-fertilizer map:" -> {
                activeMapping = soilToFertilizer
            }

            line == "fertilizer-to-water map:" -> {
                activeMapping = fertilizerToWater
            }

            line == "water-to-light map:" -> {
                activeMapping = waterToLight
            }

            line == "light-to-temperature map:" -> {
                activeMapping = lightToTemperature
            }

            line == "temperature-to-humidity map:" -> {
                activeMapping = temperatureToHumidity
            }

            line == "humidity-to-location map:" -> {
                activeMapping = humidityToLocation
            }

            line.isBlank() -> {
                // no-op
            }

            else -> {
                val (destinationStart, sourceStart, rangeLength) = line.split(" ", limit = 3)

                activeMapping.add(
                    Mapping(
                        destinationStart.toLong(),
                        sourceStart.toLong(),
                        rangeLength.toLong(),
                    )
                )
            }
        }
    }

    return Almanac(
        seeds = seeds.toSet(),
        seedRanges = seedRanges,
        seedToSoil = MappingTable(seedToSoil),
        soilToFertilizer = MappingTable(soilToFertilizer),
        fertilizerToWater = MappingTable(fertilizerToWater),
        waterToLight = MappingTable(waterToLight),
        lightToTemperature = MappingTable(lightToTemperature),
        temperatureToHumidity = MappingTable(temperatureToHumidity),
        humidityToLocation = MappingTable(humidityToLocation),
    )
}

fun Almanac.findClosestLocationFromSeeds(): Long {
    return seeds.minOf { seed ->
        humidityToLocation.map(
            temperatureToHumidity.map(
                lightToTemperature.map(
                    waterToLight.map(
                        fertilizerToWater.map(
                            soilToFertilizer.map(
                                seedToSoil.map(seed)
                            )
                        )
                    )
                )
            )
        )
    }
}

fun Almanac.findClosestLocationFromSeedRanges(): Long {
    var location: Long = -1

    while (true) {
        location += 1

        val humidity = findSourceOrNull(location, humidityToLocation) ?: continue
        val temperature = findSourceOrNull(humidity, temperatureToHumidity) ?: continue
        val light = findSourceOrNull(temperature, lightToTemperature) ?: continue
        val water = findSourceOrNull(light, waterToLight) ?: continue
        val fertilizer = findSourceOrNull(water, fertilizerToWater) ?: continue
        val soil = findSourceOrNull(fertilizer, soilToFertilizer) ?: continue
        val seed = findSourceOrNull(soil, seedToSoil) ?: continue

        if (seedRanges.any { range -> seed in range }) {
            return location
        }
    }
}

private fun findSourceOrNull(destination: Long, mappingTable: MappingTable): Long? {
    val mapping = mappingTable.rows
        .find { mapping -> destination in mapping.destinationRange }

    return when {
        mapping != null ->
            mapping.sourceRangeStart + (destination - mapping.destinationRangeStart)

        mappingTable.rows.none { tth -> destination in tth.sourceRange } ->
            destination

        else -> null
    }
}

fun part1(input: String): Long {
    val almanac = parseAlmanac(input)

    return almanac.findClosestLocationFromSeeds()
}

fun part2(input: String): Long {
    val almanac = parseAlmanac(input)

    return almanac.findClosestLocationFromSeedRanges()
}

fun main() {
    val testInput = readInput("day05/test-input.txt")
    val input = readInput("day05/input.txt")

    // part 1
    checkSolution(part1(testInput), 35)
    println(part1(input))

    // part 2
    checkSolution(part2(testInput), 46)
    println(part2(input))
}
