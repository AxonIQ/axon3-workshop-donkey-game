package org.donkeygame.core

import org.axonframework.commandhandling.TargetAggregateIdentifier

data class CreateGameOfDonkeyCommand(val matchName: String)

data class StartGameOfDonkeyCommand(@TargetAggregateIdentifier val aggregateId: String)

data class JoinGameOfDonkeyCommand(
        @TargetAggregateIdentifier val aggregateId: String,
        val userName: String
)

data class PlayCardCommand(
        @TargetAggregateIdentifier val aggregateId: String,
        val userName: String,
        val cardNumber: Int
)

data class CallGameFinishedCommand(
        @TargetAggregateIdentifier val aggregateId: String,
        val userName: String
)
