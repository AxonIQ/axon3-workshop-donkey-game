package org.donkeygame.core

import org.axonframework.commandhandling.TargetAggregateIdentifier

data class CreateGameCommand(val matchName: String)
data class GameCreatedEvent(val matchName: String)

data class JoinGameCommand(
        @TargetAggregateIdentifier val matchName: String,
        val playerName: String
)

data class PlayerJoinedEvent(
        val matchName: String,
        val playerName: String
)
data class CardsDealtForPlayerEvent(
        val matchName: String,
        val playerName: String,
        val cards: List<Card>
)

data class StartGameCommand(@TargetAggregateIdentifier val matchName: String)
data class GameStartedEvent(val matchName: String)

data class SelectCardCommand(
        @TargetAggregateIdentifier val matchName: String,
        val playerName: String,
        val cardIndex: Int
)

data class CardSelectedEvent(
        val matchName: String,
        val playerName: String,
        val selectedCard: Card
)

data class CardReceivedEvent(
        val matchName: String,
        val playerName: String,
        val playedCard: Card
)

data class CallGameFinishedCommand(
        @TargetAggregateIdentifier val matchName: String,
        val playerName: String
)

data class FinishedCalledEvent(val matchName: String, val playerName: String)
