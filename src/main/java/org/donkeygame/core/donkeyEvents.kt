package org.donkeygame.core

data class GameOfDonkeyCreatedEvent(val matchName: String)

data class GameOfDonkeyStartedEvent(val matchName: String)

data class GameOfDonkeyJoinedEvent(
        val matchName: String,
        val userName: String
)

data class CardsDealtForPlayerEvent(
        val matchName: String,
        val userName: String,
        val cards: List<String>
)

data class CardPlayedEvent(
        val matchName: String,
        val userName: String,
        val cardNumber: Int
)

data class FinishedCalledEvent(val matchName: String)
