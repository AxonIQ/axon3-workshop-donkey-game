package org.donkeygame.ui

import com.fasterxml.jackson.annotation.JsonProperty

data class CreateGameOfDonkeyRequest(@JsonProperty("matchName") val matchName: String)

data class JoinGameOfDonkeyRequest(
        @JsonProperty("matchName") val matchName: String,
        @JsonProperty("playerName") val playerName: String
)

data class StartGameOfDonkeyRequest(@JsonProperty("matchName") val matchName: String)

data class PlayCardRequest(
        @JsonProperty("matchName") val matchName: String,
        @JsonProperty("playerName") val playerName: String,
        @JsonProperty("cardNumber") val cardNumber: Int
)

data class CallGameFinishedRequest(
        @JsonProperty("matchName") val matchName: String,
        @JsonProperty("playerName") val playerName: String
)

