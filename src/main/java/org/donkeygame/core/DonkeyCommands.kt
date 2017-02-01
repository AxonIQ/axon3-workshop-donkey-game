package org.donkeygame.core

import org.axonframework.commandhandling.TargetAggregateIdentifier

data class CreateGameOfDonkeyCommand(val matchName: String)

data class StartGameOfDonkeyCommand(@TargetAggregateIdentifier val matchName: String)

data class JoinGameOfDonkeyCommand(
        @TargetAggregateIdentifier val matchName: String,
        val playerName: String
)

data class PlayCardCommand(
        @TargetAggregateIdentifier val matchName: String,
        val playerName: String,
        val cardNumber: Int
)

data class CallGameFinishedCommand(
        @TargetAggregateIdentifier val matchName: String,
        val playerName: String
)
