package org.donkeygame.core

enum class Suit {
    SPADES,
    HEARTS,
    DIAMONDS,
    CLUBS
}

enum class Rank {
    ACE,
    KING,
    QUEEN,
    JACK,
    TEN,
    NINE,
    EIGHT,
    SEVEN,
    SIX,
    FIVE,
    FOUR,
    THREE,
    DEUCE
}

data class Card(
        val suit: Suit,
        val rank: Rank
)