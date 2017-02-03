package org.donkeygame.domain;

import org.axonframework.test.aggregate.AggregateTestFixture;
import org.axonframework.test.aggregate.FixtureConfiguration;
import org.donkeygame.core.*;
import org.junit.Before;
import org.junit.Test;

import static org.axonframework.test.matchers.Matchers.*;
import static org.hamcrest.Matchers.isA;

public class DonkeyTest {

    private FixtureConfiguration fixture;

    @Before
    public void setUp() throws Exception {
        fixture = new AggregateTestFixture<>(Donkey.class);
    }

    @Test
    public void testGameStart() {
        fixture.given(new GameCreatedEvent("testGame"),
                      new PlayerJoinedEvent("testGame", "player1"),
                      new PlayerJoinedEvent("testGame", "player2"),
                      new PlayerJoinedEvent("testGame", "player3"))
                .when(new StartGameCommand("testGame"))
                .expectEventsMatching(exactSequenceOf(
                        messageWithPayload(equalTo(new GameStartedEvent("testGame"))),
                        messageWithPayload(isA(CardsDealtForPlayerEvent.class)),
                        messageWithPayload(isA(CardsDealtForPlayerEvent.class)),
                        messageWithPayload(isA(CardsDealtForPlayerEvent.class)),
                        andNoMore()
                ));
    }

    // TODO: Try to implement a test (and feature) where the game can only be started when 3 players have joined

}
