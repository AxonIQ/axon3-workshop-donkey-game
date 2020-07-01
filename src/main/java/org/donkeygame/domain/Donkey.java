package org.donkeygame.domain;

import org.axonframework.commandhandling.CommandHandler;
import org.axonframework.commandhandling.model.AggregateIdentifier;
import org.axonframework.eventsourcing.EventSourcingHandler;
import org.axonframework.spring.stereotype.Aggregate;
import org.donkeygame.core.Card;
import org.donkeygame.core.CardReceivedEvent;
import org.donkeygame.core.CardSelectedEvent;
import org.donkeygame.core.CardsDealtForPlayerEvent;
import org.donkeygame.core.CreateGameCommand;
import org.donkeygame.core.GameCreatedEvent;
import org.donkeygame.core.GameStartedEvent;
import org.donkeygame.core.JoinGameCommand;
import org.donkeygame.core.PlayerJoinedEvent;
import org.donkeygame.core.Rank;
import org.donkeygame.core.SelectCardCommand;
import org.donkeygame.core.StartGameCommand;
import org.donkeygame.core.Suit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Set;
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
    private Map<String, List<Card>> handPerPlayer = new HashMap<>();
    private Map<String, Card> playedCards = new HashMap<>();

    public Donkey() {
        // Default constructor
    }

    @CommandHandler
    public Donkey(CreateGameCommand cmd) {
        apply(new GameCreatedEvent(cmd.getMatchName()));
    }

    @CommandHandler
    public void handle(JoinGameCommand cmd) {
        apply(new PlayerJoinedEvent(cmd.getMatchName(), cmd.getPlayerName()));
    }

    @CommandHandler
    public void handle(StartGameCommand cmd) {
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

        apply(new GameStartedEvent(matchName));

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
                player -> Arrays
                        .asList(cardIterator.next(), cardIterator.next(), cardIterator.next(), cardIterator.next())
        ));
    }

    @CommandHandler
    public void handle(SelectCardCommand cmd) {
        String playerName = cmd.getPlayerName();
        String nextPlayer = playerAfter(playerName);
        if (playedCards.containsKey(nextPlayer)) {
            logger.info("matchName[" + matchName + "] - " +
                                "Player [" + playerName + "] has already selected a card for player [" + nextPlayer
                                + "]");
            return;
        }

        Card selectedCard = handPerPlayer.get(playerName).get(cmd.getCardIndex());
        apply(new CardSelectedEvent(cmd.getMatchName(), playerName, selectedCard));
    }

    // TODO: Implement the CallGameFinishedCommand handling here

    @EventSourcingHandler
    public void on(GameCreatedEvent event) {
        matchName = event.getMatchName();
    }

    @EventSourcingHandler
    public void on(PlayerJoinedEvent event) {
        players.add(event.getPlayerName());
    }

    @EventSourcingHandler
    public void on(CardsDealtForPlayerEvent event) {
        handPerPlayer.put(event.getPlayerName(), new ArrayList<>(event.getCards()));
    }

    @EventSourcingHandler
    public void on(CardSelectedEvent event) {
        String nextPlayer = playerAfter(event.getPlayerName());
        updatePlayerHands(event.getPlayerName(), nextPlayer, event.getSelectedCard());
        updatePlayedCards(nextPlayer, event.getSelectedCard());

        if (allPlayersPlayedACard()) {
            players.forEach(playerName -> apply(new CardReceivedEvent(matchName,
                                                                      playerName,
                                                                      playedCards.get(playerName))));
            playedCards.clear();
        }
    }

    private String playerAfter(String playerName) {
        ArrayList<String> playersList = new ArrayList<>(players);
        int playerIndex = playersList.indexOf(playerName) + 1;
        int numberOfPlayers = playersList.size();
        int nextPlayerIndex = ((numberOfPlayers + playerIndex) % numberOfPlayers);
        return playersList.get(nextPlayerIndex);
    }

    private void updatePlayerHands(String playerName, String nextPlayer, Card selectedCard) {
        List<Card> playersCards = handPerPlayer.get(playerName);
        playersCards.remove(selectedCard);
        handPerPlayer.get(nextPlayer).add(selectedCard);
    }

    private void updatePlayedCards(String playerName, Card selectedCard) {
        playedCards.put(playerName, selectedCard);
    }

    private boolean allPlayersPlayedACard() {
        return playedCards.size() == players.size();
    }
}
