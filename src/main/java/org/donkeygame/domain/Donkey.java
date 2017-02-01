package org.donkeygame.domain;

import org.axonframework.commandhandling.CommandHandler;
import org.axonframework.commandhandling.model.AggregateIdentifier;
import org.axonframework.eventsourcing.EventSourcingHandler;
import org.axonframework.spring.stereotype.Aggregate;
import org.donkeygame.core.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static org.axonframework.commandhandling.model.AggregateLifecycle.apply;

@Aggregate
public class Donkey {

    private static final Logger logger = LoggerFactory.getLogger(Donkey.class);

    private static final int MINIMUM_NUMBER_PLAYERS = 1;
    private static final int MAXIMUM_NUMBER_PLAYERS = 13;

    @AggregateIdentifier
    private String matchName;
    private Set<String> players = new HashSet<>();
    private Map<String, List<Card>> cardsPerPlayer = new HashMap<>();
    private Map<String, Card> playedCards = new HashMap<>();

    public Donkey() {
        // Default constructor
    }

    @CommandHandler
    public Donkey(CreateGameOfDonkeyCommand cmd) {
        apply(new GameOfDonkeyCreatedEvent(cmd.getMatchName()));
    }

    @CommandHandler
    public void handle(JoinGameOfDonkeyCommand cmd) {
        apply(new GameOfDonkeyJoinedEvent(cmd.getMatchName(), cmd.getPlayerName()));
    }

    @CommandHandler
    public void handle(StartGameOfDonkeyCommand cmd) {
        if (players.size() < MINIMUM_NUMBER_PLAYERS) {
            logger.info(
                    "matchName[" + matchName + "] - " +
                            "At least " + MINIMUM_NUMBER_PLAYERS + " player needs to participate in a game of Donkey"
            );
            return;
        } else if (players.size() > MAXIMUM_NUMBER_PLAYERS) {
            logger.info(
                    "matchName[" + matchName + "] - " +
                            "At most " + MAXIMUM_NUMBER_PLAYERS + " player can participate in a game of Donkey"
            );
            return;
        }

        apply(new GameOfDonkeyStartedEvent(matchName));

        dealCards().forEach((player, cards) -> apply(new CardsDealtForPlayerEvent(matchName, player, cards)));
    }

    private Map<String, List<Card>> dealCards() {
        List<Card> cards = Arrays.stream(Rank.values())
                .limit(players.size())
                .flatMap(r -> Arrays.stream(Suit.values()).map(s -> new Card(s, r)))
                .collect(Collectors.toList());

        Collections.shuffle(cards);

        ListIterator<Card> cardIterator = cards.listIterator();

        return players.stream().collect(Collectors.toMap(
                Function.identity(),
                player -> Arrays.asList(cardIterator.next(), cardIterator.next(), cardIterator.next(), cardIterator.next())
        ));
    }

    @CommandHandler
    public void handle(SelectCardCommand cmd) {
        String playerName = cmd.getPlayerName();
        if (playedCards.containsKey(playerName)) {
            logger.info("matchName[" + matchName + "] - Player [" + playerName + "] has already selected a card");
            return;
        }

        Card selectedCard = cardsPerPlayer.get(playerName).get(cmd.getCardIndex());
        apply(new CardSelectedEvent(cmd.getMatchName(), playerName, selectedCard));
    }

    @CommandHandler
    public void handle(CallGameFinishedCommand cmd) {
        logger.info("reached CallGameFinishedCommand");
        //TODO finished logic
    }

    @EventSourcingHandler
    public void on(GameOfDonkeyCreatedEvent event) {
        matchName = event.getMatchName();
    }

    @EventSourcingHandler
    public void on(GameOfDonkeyJoinedEvent event) {
        players.add(event.getPlayerName());
    }

    @EventSourcingHandler
    public void on(CardsDealtForPlayerEvent event) {
        cardsPerPlayer.put(event.getPlayerName(), new ArrayList<>(event.getCards()));
    }

    @EventSourcingHandler
    public void on(CardSelectedEvent event) {
        updateCardsPerPlayer(event.getPlayerName(), event.getSelectedCard());
        updatePlayedCards(event.getPlayerName(), event.getSelectedCard());

        if (everybodySelectedACard()) {
            players.forEach(playerName -> apply(new CardsPlayedEvent(matchName, playerName, playedCards)));
            playedCards.clear();
        }
    }

    private void updateCardsPerPlayer(String playerName, Card selectedCard) {
        List<Card> playersCards = cardsPerPlayer.get(playerName);
        playersCards.remove(selectedCard);
    }

    private void updatePlayedCards(String playerName, Card selectedCard) {
        playedCards.put(playerName, selectedCard);
    }

    private boolean everybodySelectedACard() {
        return playedCards.size() == players.size();
    }

}
