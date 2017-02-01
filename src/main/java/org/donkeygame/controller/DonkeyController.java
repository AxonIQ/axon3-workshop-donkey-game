package org.donkeygame.controller;

import org.axonframework.commandhandling.gateway.CommandGateway;
import org.donkeygame.core.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@Controller
public class DonkeyController {

    private final CommandGateway commandGateway;

    @Autowired
    public DonkeyController(CommandGateway commandGateway) {
        this.commandGateway = commandGateway;
    }

    @MessageMapping("/create-match")
    @SendTo("/topic/matches")
    public GameOfDonkeyResponse createDonkeyGame(CreateGameOfDonkeyRequest msg) throws ExecutionException, InterruptedException {
        CreateGameOfDonkeyCommand command = new CreateGameOfDonkeyCommand(msg.getMatchName());
        return commandGateway.<String>send(command)
                .thenApply(matchId -> new GameOfDonkeyResponse(matchId, msg.getMatchName()))
                .get();
    }

    @MessageMapping("/join")
    @SendTo("/topic/joined")
    public CompletableFuture<Object> joinGameOfDonkey(JoinGameOfDonkeyRequest msg) {
        return commandGateway.send(new JoinGameOfDonkeyCommand(msg.getAggregateId(), msg.getUserName()));
    }

    @MessageMapping("/start-match")
    @SendTo("/topic/start-match")
    public CompletableFuture<Object> startGameOfDonkey(StartGameOfDonkeyRequest msg) {
        return commandGateway.send(new StartGameOfDonkeyCommand(msg.getAggregateId()));
    }

    @MessageMapping("/play-card")
    @SendTo("/topic/play-card")
    public CompletableFuture<Object> playCard(PlayCardRequest msg) {
        return commandGateway.send(new PlayCardCommand(msg.getAggregateId(), msg.getUserName(), msg.getCardNumber()));
    }

    @MessageMapping("/call-finished")
    @SendTo("/topic/call-finished")
    public CompletableFuture<Object> callGameFinished(CallGameFinishedRequest msg) {
        return commandGateway.send(new CallGameFinishedCommand(msg.getAggregateId(), msg.getUserName()));
    }

}
