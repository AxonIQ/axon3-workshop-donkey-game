package org.donkeygame.controller

data class CreateDonkeyGame(
        val matchName: String
)

data class JoinGameOfDonkey(
        val aggregateId: String,
        val userName: String
)

data class StartGameOfDonkey(val aggregateId: String)

data class PlayCard(
        val aggregateId: String,
        val userName: String,
        val cardNumber: Int
)

data class CallGameFinished(val aggregateId: String)

