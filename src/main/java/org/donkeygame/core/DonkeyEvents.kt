package org.donkeygame.core

data class GameCreatedEvent(val matchName: String)

data class GameStartedEvent(val matchName: String)

data class GameJoinedEvent(
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
        val selectedCard: Card
)

data class CardPlayedEvent(
        val matchName: String,
        val playerName: String,
        val playedCard: Card
)

data class FinishedCalledEvent(val matchName: String)
