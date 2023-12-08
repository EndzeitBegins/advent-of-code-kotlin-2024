package io.github.endzeitbegins.aoc2023.day07

import io.github.endzeitbegins.aoc2023.checkSolution
import io.github.endzeitbegins.aoc2023.pow
import io.github.endzeitbegins.aoc2023.readInput

private fun determineHandType(cards: String, supportJoker: Boolean): Hand.Type {
    val cardCounts = if(supportJoker) {
        val cardsWithoutJoker = cards.replace("J", "")
        val temporaryCardCounts = cardsWithoutJoker
            .groupingBy{ it }
            .eachCount()
            .values
            .sorted()
            .ifEmpty { listOf(0) }

        val most = temporaryCardCounts.last()
        temporaryCardCounts.dropLast(1) + (most + cards.length - cardsWithoutJoker.length)
    } else {
        cards
            .groupingBy{ it }
            .eachCount()
            .values
    }

    return when {
        5 in cardCounts -> Hand.Type.FIVE_OF_A_KIND
        4 in cardCounts -> Hand.Type.FOUR_OF_A_KIND
        3 in cardCounts && 2 in cardCounts -> Hand.Type.FULL_HOUSE
        3 in cardCounts -> Hand.Type.THREE_OF_A_KIND
        cardCounts.count { it == 2 } == 2 -> Hand.Type.TWO_PAIR
        2 in cardCounts -> Hand.Type.ONE_PAIR
        else -> Hand.Type.HIGH_CARD
    }
}

fun getCardValue(card: Char, supportJoker: Boolean): Long = when(card) {
    'A' -> 14
    'K' -> 13
    'Q' -> 12
    'J' -> if(supportJoker) 1 else 11
    'T' -> 10
    '9' -> 9
    '8' -> 8
    '7' -> 7
    '6' -> 6
    '5' -> 5
    '4' -> 4
    '3' -> 3
    '2' -> 2
    else -> throw IllegalArgumentException("Unsupported card '$card'")
}

data class Hand(val cards: String,
                val supportJoker: Boolean,
): Comparable<Hand> {
    init { require(cards.length == 5) }

    override fun compareTo(other: Hand): Int {
        return this.value.compareTo(other.value)
    }

    val type: Type = determineHandType(cards, supportJoker)
    private val value: Long = determineValue()

    private fun determineValue(): Long {
        var value: Long = 0

        cards.forEachIndexed { index, card ->
            value += 15.pow(cards.length - index) * getCardValue(card, supportJoker)
        }

        value += when(type) {
            Type.FIVE_OF_A_KIND -> 6
            Type.FOUR_OF_A_KIND -> 5
            Type.FULL_HOUSE -> 4
            Type.THREE_OF_A_KIND -> 3
            Type.TWO_PAIR -> 2
            Type.ONE_PAIR -> 1
            Type.HIGH_CARD -> 0
        } * 15.pow(cards.length + 3)

        return value
    }

    enum class Type {
        FIVE_OF_A_KIND,
        FOUR_OF_A_KIND,
        FULL_HOUSE,
        THREE_OF_A_KIND,
        TWO_PAIR,
        ONE_PAIR,
        HIGH_CARD
    }
}

data class BiddedHand(
    val hand: Hand,
    val bid: Long,
)

fun parseBiddedHands(input: String, supportJoker: Boolean = false): List<BiddedHand> {
    return input
        .lines()
        .map { line ->
            val (hand, bid) = line.split(" ", limit = 2)

            BiddedHand(
                hand = Hand(hand, supportJoker),
                bid = bid.toLong()
            )
        }
}

private fun calculateTotalWinnings(biddedHands: List<BiddedHand>): Long {
    return biddedHands
        .sortedBy { it.hand }
        .foldIndexed(0L) { index, winnings, biddedHand ->
            // println("${biddedHand.hand.cards};${biddedHand.hand.type};$winnings;$index;${biddedHand.bid};+${biddedHand.bid * (index + 1)}")
            winnings + biddedHand.bid * (index + 1)
        }
}

fun part1(input: String): Long {
    val biddedHands = parseBiddedHands(input)

    return calculateTotalWinnings(biddedHands)
}

fun part2(input: String): Long {
    val biddedHandsWithJoker = parseBiddedHands(input, supportJoker = true)

    return calculateTotalWinnings(biddedHandsWithJoker)
}

fun main() {
    val testInput = readInput("day07/test-input.txt")
    val input = readInput("day07/input.txt")

    // part 1
    checkSolution(part1(testInput), 6440)
    println(part1(input))

    // part 2
     checkSolution(part2(testInput), 5905)
     println(part2(input))
}
