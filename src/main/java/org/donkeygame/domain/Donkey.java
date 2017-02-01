package org.donkeygame.domain;

import org.axonframework.commandhandling.CommandHandler;
import org.axonframework.commandhandling.model.AggregateIdentifier;
import org.axonframework.eventsourcing.EventSourcingHandler;
import org.axonframework.spring.stereotype.Aggregate;
import org.donkeygame.core.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

import static org.axonframework.commandhandling.model.AggregateLifecycle.apply;

@Aggregate
public class Donkey {

    private static final Logger logger = LoggerFactory.getLogger(Donkey.class);

    private static final int MINIMUM_NUMBER_PLAYERS = 1;

    @AggregateIdentifier
    private String matchName;
    private Set<String> players = new HashSet<>();

    @CommandHandler
    public Donkey(CreateGameOfDonkeyCommand cmd) {
        apply(new GameOfDonkeyCreatedEvent(cmd.getMatchName()));
    }

    @CommandHandler
    public void handle(JoinGameOfDonkeyCommand cmd) {
        apply(new GameOfDonkeyJoinedEvent(cmd.getMatchName(), cmd.getUserName()));
    }

    @CommandHandler
    public void handle(StartGameOfDonkeyCommand cmd) {
        if (players.size() < MINIMUM_NUMBER_PLAYERS) {
            logger.info(
                    "matchName[" + matchName + "] - " +
                    "At least " + MINIMUM_NUMBER_PLAYERS + " player needs to participate in a game of Donkey"
            );
        }

        apply(new GameOfDonkeyStartedEvent(matchName));
    }

    @CommandHandler
    public void handle(PlayCardCommand cmd) {
        //TODO playing logic
    }

    @CommandHandler
    public void handle(CallGameFinishedCommand cmd) {
        //TODO finished logic
    }

    @EventSourcingHandler
    public void on(GameOfDonkeyCreatedEvent event) {
        matchName = event.getMatchName();
    }

    @EventSourcingHandler
    public void on(GameOfDonkeyJoinedEvent event) {
        players.add(event.getUserName());
    }

    @EventSourcingHandler
    public void on(GameOfDonkeyStartedEvent event) {
        //TODO replace for actual dealing action
        List<String> cards = Collections.emptyList();

        players.forEach(player -> apply(new CardsDealtForPlayerEvent(matchName, player, cards)));
    }

    @EventSourcingHandler
    public void on(CardsDealtForPlayerEvent event) {
        updateCardSetup();
    }

    @EventSourcingHandler
    public void on(CardPlayedEvent event) {

        updateCardSetup();
    }

    private void updateCardSetup() {
        //TODO Store card setup in aggregate
    }

}
