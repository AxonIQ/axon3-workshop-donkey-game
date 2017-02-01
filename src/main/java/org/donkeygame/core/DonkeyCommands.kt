package org.donkeygame.core

import org.axonframework.commandhandling.TargetAggregateIdentifier

data class CreateGameOfDonkeyCommand(val matchName: String)

data class StartGameOfDonkeyCommand(@TargetAggregateIdentifier val matchName: String)

data class JoinGameOfDonkeyCommand(
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
