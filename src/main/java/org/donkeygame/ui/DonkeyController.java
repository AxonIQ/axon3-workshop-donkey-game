package org.donkeygame.ui;

import org.axonframework.commandhandling.gateway.CommandGateway;
import org.axonframework.eventhandling.EventHandler;
import org.donkeygame.core.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
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

    private final Map<String, Set<String>> playersPerMatch;

    @Autowired
    public DonkeyController(CommandGateway commandGateway, SimpMessagingTemplate messagingTemplate) {
        this.commandGateway = commandGateway;
        this.messagingTemplate = messagingTemplate;

        playersPerMatch = new HashMap<>();
    }

    @MessageMapping("/create-match")
    public void createDonkeyGame(CreateGameRequest msg) throws ExecutionException, InterruptedException {
        commandGateway.send(new CreateGameCommand(msg.getMatchName()));
    }

    @MessageMapping("/join-match")
    public void joinGameOfDonkey(JoinGameRequest msg) {
        commandGateway.send(new JoinGameCommand(msg.getMatchName(), msg.getPlayerName()));
    }

    @MessageMapping("/start-match")
    public void startGameOfDonkey(StartGameRequest msg) {
        commandGateway.send(new StartGameCommand(msg.getMatchName()));
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
    public void on(GameCreatedEvent event) {
        messagingTemplate.convertAndSend(ALERT_PATH, new AlertResponse(SUCCESS, "Match [" + event.getMatchName() + "] has been created"));
    }

    @EventHandler
    public void on(GameJoinedEvent event) {
        messagingTemplate.convertAndSend(ALERT_PATH, new AlertResponse(SUCCESS, "Player [" + event.getPlayerName() + "] has successfully joined the match [" + event.getMatchName() + "]"));

        Set<String> playersForMatch = playersPerMatch.computeIfAbsent(event.getMatchName(), matchName -> new HashSet<>());
        playersForMatch.add(event.getPlayerName());
        messagingTemplate.convertAndSend(buildDestination(event.getMatchName()), new JoinedResponse(playersForMatch));
    }

    @EventHandler
    public void on(GameStartedEvent event) {
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

    //TODO Replace the Object parameter for your own event
    @EventHandler
    public void onFinishToggled(/*Object finishToggledEvent*/) {
        boolean canFinish = true; // Retrieve from event
        String destination = buildDestination("matchName", "playerName");
        messagingTemplate.convertAndSend(
                destination, new ToggleFinishButtonResponse(canFinish)
        );
    }

    //TODO Replace the Object parameter for your own event
    @EventHandler
    public void onOutcomeEvent(/*Object outcomeEvent*/) {
        Map<String, Boolean> outcomePerPlayer = new HashMap<>(); // Retrieve from event
        outcomePerPlayer.forEach((player, outcome) -> messagingTemplate.convertAndSend(
                buildDestination("matchName", player), new OutcomeResponse(outcome)
        ));
    }

    private String buildDestination(String matchName, String playerName) {
        return buildDestination(matchName) + PLAYER_PATH + playerName;
    }

    private String buildDestination(String matchName) {
        return MATCH_PATH + matchName;
    }

}
