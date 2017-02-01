package org.donkeygame.core

data class GameOfDonkeyCreatedEvent(val aggregateId: String, val matchName: String)

data class GameOfDonkeyStartedEvent(val aggregateId: String)

data class GameOfDonkeyJoinedEvent(
        val aggregateId: String,
        val userName: String
)

data class CardsDealtForPlayerEvent(
        val aggregateId: String,
        val userName: String,
        val cards: List<String>
)

data class CardPlayedEvent(
        val aggregateId: String,
        val userName: String,
        val cardNumber: Int
)

data class FinishedCalledEvent(val aggregateId: String)
