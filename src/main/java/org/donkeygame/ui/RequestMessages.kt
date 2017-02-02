package org.donkeygame.ui

import com.fasterxml.jackson.annotation.JsonProperty

data class CreateGameRequest(@JsonProperty("matchName") val matchName: String)

data class JoinGameRequest(
        @JsonProperty("matchName") val matchName: String,
        @JsonProperty("playerName") val playerName: String
)

data class StartGameRequest(@JsonProperty("matchName") val matchName: String)

data class SelectCardRequest(
        @JsonProperty("matchName") val matchName: String,
        @JsonProperty("playerName") val playerName: String,
        @JsonProperty("cardIndex") val cardIndex: Int
)

data class CallGameFinishedRequest(
        @JsonProperty("matchName") val matchName: String,
        @JsonProperty("playerName") val playerName: String
)

