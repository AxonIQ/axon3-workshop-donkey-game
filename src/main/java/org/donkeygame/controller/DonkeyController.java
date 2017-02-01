package org.donkeygame.controller;

import org.axonframework.commandhandling.gateway.CommandGateway;
import org.donkeygame.core.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

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
        return commandGateway.<String>send(new CreateGameOfDonkeyCommand(msg.getMatchName()))
                .thenApply(GameOfDonkeyResponse::new)
                .get();
    }

    @MessageMapping("/join-match")
    public void joinGameOfDonkey(JoinGameOfDonkeyRequest msg) {
        commandGateway.send(new JoinGameOfDonkeyCommand(msg.getMatchName(), msg.getUserName()));
    }

    @MessageMapping("/start-match")
    public void startGameOfDonkey(StartGameOfDonkeyRequest msg) {
        commandGateway.send(new StartGameOfDonkeyCommand(msg.getMatchName()));
    }

    @MessageMapping("/play-card")
    public void playCard(PlayCardRequest msg) {
        commandGateway.send(new PlayCardCommand(msg.getMatchName(), msg.getUserName(), msg.getCardNumber()));
    }

    @MessageMapping("/call-finished")
    public void callGameFinished(CallGameFinishedRequest msg) {
        commandGateway.send(new CallGameFinishedCommand(msg.getMatchName(), msg.getUserName()));
    }

}
