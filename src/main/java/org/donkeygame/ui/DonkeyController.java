package org.donkeygame.ui;

import org.axonframework.commandhandling.gateway.CommandGateway;
import org.axonframework.eventhandling.EventHandler;
import org.donkeygame.core.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ExecutionException;

@Controller
public class DonkeyController {

    private static final String BASE_PATH = "/topic/";
    private static final String ALERT_PATH = BASE_PATH + "/alerts";
    private static final String MATCH_PATH = BASE_PATH + "/match/";
    private static final String PLAYER_PATH = "/player/";

    private static final boolean SUCCESS = true;

    private final CommandGateway commandGateway;
    private final SimpMessagingTemplate messagingTemplate;

    private final Set<String> players;

    @Autowired
    public DonkeyController(CommandGateway commandGateway, SimpMessagingTemplate messagingTemplate) {
        this.commandGateway = commandGateway;
        this.messagingTemplate = messagingTemplate;

        players = new HashSet<>();
    }

    @MessageMapping("/create-match")
    public void createDonkeyGame(CreateGameOfDonkeyRequest msg) throws ExecutionException, InterruptedException {
        commandGateway.send(new CreateGameOfDonkeyCommand(msg.getMatchName()));
    }

    @MessageMapping("/join-match")
    public void joinGameOfDonkey(JoinGameOfDonkeyRequest msg) {
        commandGateway.send(new JoinGameOfDonkeyCommand(msg.getMatchName(), msg.getPlayerName()));
    }

    @MessageMapping("/start-match")
    public void startGameOfDonkey(StartGameOfDonkeyRequest msg) {
        commandGateway.send(new StartGameOfDonkeyCommand(msg.getMatchName()));
    }

    @MessageMapping("/select-card")
    public void selectCard(SelectCardRequest msg) {
        commandGateway.send(new SelectCardCommand(msg.getMatchName(), msg.getPlayerName(), msg.getCardIndex()));
    }

    @MessageMapping("/call-finished")
    public void callGameFinished(CallGameFinishedRequest msg) {
        commandGateway.send(new CallGameFinishedCommand(msg.getMatchName(), msg.getPlayerName()));
    }


    @EventHandler
    public void on(GameOfDonkeyCreatedEvent event) {
        messagingTemplate.convertAndSend(ALERT_PATH, new AlertResponse(SUCCESS, "Match [" + event.getMatchName() + "] has been created"));
    }

    @EventHandler
    public void on(GameOfDonkeyJoinedEvent event) {
        messagingTemplate.convertAndSend(ALERT_PATH, new AlertResponse(SUCCESS, "Player [" + event.getPlayerName() + "] has successfully joined the match [" + event.getMatchName() + "]"));

        players.add(event.getPlayerName());
        messagingTemplate.convertAndSend(buildDestination(event.getMatchName()), new JoinedResponse(players));
    }

    @EventHandler
    public void on(GameOfDonkeyStartedEvent event) {
        messagingTemplate.convertAndSend(ALERT_PATH, new AlertResponse(SUCCESS, "The match [" + event.getMatchName() + "] has successfully started"));
    }

    @EventHandler
    public void on(CardsDealtForPlayerEvent event) {
        messagingTemplate.convertAndSend(buildDestination(event.getMatchName(), event.getPlayerName()), new HandResponse(event.getCards()));
    }

    @EventHandler
    public void on(CardPlayedEvent event) {
        messagingTemplate.convertAndSend(
                buildDestination(event.getMatchName(), event.getPlayerName()), new PlayedCardResponse(event.getPlayedCard())
        );
    }

    @EventHandler
    public void on(Object finishedPossibilityEvent) {
        boolean canFinish = true;
        String destination = buildDestination("matchName", "playerName");
        messagingTemplate.convertAndSend(
                destination, new FinishPossibilityResponse(canFinish)
        );
    }

    private String buildDestination(String matchName, String playerName) {
        return buildDestination(matchName) + PLAYER_PATH + playerName;
    }

    private String buildDestination(String matchName) {
        return MATCH_PATH + matchName;
    }

}
