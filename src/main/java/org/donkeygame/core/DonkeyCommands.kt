package org.donkeygame.core

import org.axonframework.commandhandling.TargetAggregateIdentifier

data class CreateGameCommand(val matchName: String)

data class StartGameCommand(@TargetAggregateIdentifier val matchName: String)

data class JoinGameCommand(
        @TargetAggregateIdentifier val matchName: String,
        val playerName: String
)

data class SelectCardCommand(
        @TargetAggregateIdentifier val matchName: String,
        val playerName: String,
        val cardIndex: Int
)

data class CallGameFinishedCommand(
        @TargetAggregateIdentifier val matchName: String,
        val playerName: String
)
