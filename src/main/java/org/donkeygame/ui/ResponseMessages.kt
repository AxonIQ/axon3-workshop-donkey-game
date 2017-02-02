package org.donkeygame.ui

import org.donkeygame.core.Card

data class JoinedResponse(val players: Set<String>)

data class AlertResponse(
        val success: Boolean,
        val response: String
)

data class HandResponse(val hand: List<Card>)

data class PlayedCardResponse(val card: Card)

data class FinishPossibilityResponse(val canFinish: Boolean)