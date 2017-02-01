package org.donkeygame.controller;

import org.axonframework.commandhandling.gateway.CommandGateway;
import org.axonframework.eventhandling.EventHandler;
import org.donkeygame.core.*;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.util.concurrent.ExecutionException;

@Controller
public class DonkeyController {

    private static final String MATCH_PATH = "/topic/match/";

    private static final boolean SUCCESS = true;

    private final CommandGateway commandGateway;
    private final SimpMessagingTemplate messagingTemplate;

    @Autowired
    public DonkeyController(CommandGateway commandGateway, SimpMessagingTemplate messagingTemplate) {
        this.commandGateway = commandGateway;
        this.messagingTemplate = messagingTemplate;
    }

    @MessageMapping("/create-match")
    @SendTo("/topic/matches")
    public GameOfDonkeyCreatedResponse createDonkeyGame(CreateGameOfDonkeyRequest msg) throws ExecutionException, InterruptedException {
        return commandGateway.<String>send(new CreateGameOfDonkeyCommand(msg.getMatchName()))
                .thenApply(GameOfDonkeyCreatedResponse::new)
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

    @EventHandler
    public void on(GameOfDonkeyJoinedEvent event) {
        messagingTemplate.convertAndSend(buildDestination(MATCH_PATH, event.getMatchName()), new GameOfDonkeyJoinedResponse(SUCCESS));
    }

    private String buildDestination(String basePath, String matchName) {
        return basePath + matchName;
    }

}
