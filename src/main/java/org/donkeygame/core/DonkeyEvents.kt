package org.donkeygame.core

data class GameOfDonkeyCreatedEvent(val matchName: String)

data class GameOfDonkeyStartedEvent(val matchName: String)

data class GameOfDonkeyJoinedEvent(
        val matchName: String,
        val playerName: String
)

data class CardsDealtForPlayerEvent(
        val matchName: String,
        val playerName: String,
        val cards: List<Card>
)

data class CardSelectedEvent(
        val matchName: String,
        val playerName: String,
        val card: String
)

data class CardsPlayedEvent(
        val matchName: String,
        val playerName: String,
        val plays : Map<String, String>
)

data class FinishedCalledEvent(val matchName: String)
