package org.donkeygame.ui;

import org.axonframework.commandhandling.gateway.CommandGateway;
import org.axonframework.eventhandling.EventHandler;
import org.donkeygame.core.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.util.concurrent.ExecutionException;

@Controller
public class DonkeyController {

    private static final String ALERT_PATH = "/topic/alerts";

    private static final boolean SUCCESS = true;

    private final CommandGateway commandGateway;
    private final SimpMessagingTemplate messagingTemplate;

    @Autowired
    public DonkeyController(CommandGateway commandGateway, SimpMessagingTemplate messagingTemplate) {
        this.commandGateway = commandGateway;
        this.messagingTemplate = messagingTemplate;
    }

    @MessageMapping("/create-match")
    public void createDonkeyGame(CreateGameOfDonkeyRequest msg) throws ExecutionException, InterruptedException {
        commandGateway.send(new CreateGameOfDonkeyCommand(msg.getMatchName()));
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
    public void on(GameOfDonkeyCreatedEvent event) {
        messagingTemplate.convertAndSend(ALERT_PATH, new AlertResponse(SUCCESS, "You've successfully created the game [" + event.getMatchName() + "]!"));
    }

    @EventHandler
    public void on(GameOfDonkeyJoinedEvent event) {
        messagingTemplate.convertAndSend(ALERT_PATH, new AlertResponse(SUCCESS, "User [" + event.getUserName() + "] successfully joined the game [" + event.getMatchName() + "]"));
    }

    @EventHandler
    public void on(GameOfDonkeyStartedEvent event) {
        messagingTemplate.convertAndSend(ALERT_PATH, new AlertResponse(SUCCESS, "You've successfully started the game [" + event.getMatchName() + "]"));
    }

    @EventHandler
    public void on(CardsPlayedEvent event) {
        event.getPlays().forEach((p, c) -> messagingTemplate.convertAndSend(buildDestination(event.getMatchName(), p), new CardPlayMessage(c)));
    }

    private String buildDestination(String matchName, String playerName) {
        return "/topic/match/" + matchName + "/user/" + playerName;
    }

}
