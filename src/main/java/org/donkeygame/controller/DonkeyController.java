package org.donkeygame.controller;

import org.axonframework.commandhandling.gateway.CommandGateway;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

@Controller
public class DonkeyController {

    private final CommandGateway commandGateway;

    @Autowired
    public DonkeyController(CommandGateway commandGateway) {
        this.commandGateway = commandGateway;
    }

    @MessageMapping("/hello-world")
    @SendTo("/topic/hello-world")
    public String createDonkeyGame(CreateDonkeyGame message) {
        return "";
    }

    @MessageMapping("/hello-world")
    @SendTo("/topic/hello-world")
    public String joinGameOfDonkey(JoinGameOfDonkey message) {
        return "";
    }

    @MessageMapping("/hello-world")
    @SendTo("/topic/hello-world")
    public String startGameOfDonkey(StartGameOfDonkey message) {
        return "";
    }

    @MessageMapping("/hello-world")
    @SendTo("/topic/hello-world")
    public String playCard(PlayCard message) {
        return "";
    }

    @MessageMapping("/hello-world")
    @SendTo("/topic/hello-world")
    public String callGameFinished(CallGameFinished message) {
        return "";
    }

}
