package org.donkeygame.core

import com.fasterxml.jackson.annotation.JsonFormat

enum class Suit {

    SPADES,
    HEARTS,
    DIAMOND,
    CLUBS

}

@JsonFormat(shape= JsonFormat.Shape.OBJECT)
enum class Rank(val letter: String) {

    ACE("A"),
    KING("K"),
    QUEEN("Q"),
    JACK("J"),
    TEN("10"),
    NINE("9"),
    EIGHT("8"),
    SEVEN("7"),
    SIX("6"),
    FIVE("5"),
    FOUR("4"),
    THREE("3"),
    DEUCE("2");

    override fun toString() = letter

}

data class Card(
        val suit: Suit,
        val rank: Rank
)