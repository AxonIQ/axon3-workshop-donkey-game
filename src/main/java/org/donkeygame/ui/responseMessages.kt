package org.donkeygame.ui

data class GameOfDonkeyCreatedResponse(val matchName: String)

data class AlertResponse(
        val success: Boolean,
        val response: String
)

data class CardPlayMessage(val card: String)