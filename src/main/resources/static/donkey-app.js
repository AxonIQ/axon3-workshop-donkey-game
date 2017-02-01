var stompClient = null;
var players = [];
var hand = null;

var joinedMatch = false;

// Connection

function connect() {
    var socket = new SockJS('/donkey-websocket');
    stompClient = Stomp.over(socket);
    stompClient.connect({}, function (frame) {
        setConnected(true);
        console.log('Connected: ' + frame);

        console.log('subscribe to [/topic/alerts]');
        stompClient.subscribe('/topic/alerts', function (alertResponse) {
            var alert = JSON.parse(alertResponse.body);
            sendAlert(alert.success, alert.response);
        });
    });
}

function disconnect() {
    if (stompClient != null) {
        stompClient.disconnect();
    }
    setConnected(false);
    console.log("Disconnected");
}

function setConnected(connected) {
    $("#connect").prop("disabled", connected);
    $("#disconnect").prop("disabled", !connected);
    if (connected) {
        $("#conversation").show();
    }
    else {
        $("#conversation").hide();
    }
    $("#greetings").html("");
}

function sendAlert(success, response) {
    alert(response);
}

// Game

function createMatch() {
    stompClient.send("/app/create-match", {}, JSON.stringify({'matchName': $("#match-name").val()}));
}

function joinMatch(request) {
    stompClient.send("/app/join-match", {}, JSON.stringify(request));

    if (!joinedMatch) {
        stompClient.subscribe('/topic/match/' + request.matchName,
            function (joinResponse) {
                players.push(
                    {
                        'playerName': JSON.parse(joinResponse.body).playerName,
                        'donkeyLevel': ""
                    }
                );
                renderPlayers(players);
            });


        stompClient.subscribe('/topic/match/' + request.matchName + '/player/' + request.playerName,
            function (handResponse) {
                hand = JSON.parse(handResponse.body).hand;
                renderHand(hand);
            });

        joinedMatch = true;
    }

}

function renderPlayers(players) {
    $.each(players, function (key, player) {
        $("#player-" + key).remove();
        $("#players").append(
            "<tr id=\"player-" + key + "\">" +
            "<td>" + player.playerName + "</td>" +
            "<td>" + player.donkeyLevel + "</td>" +
            "</tr>"
        );
    });
}

function renderHand(hand) {
    $.each(hand, function (key, card) {
        var rank = card.rank.letter;
        var suit = card.suit.toLowerCase();
        $("#cards").append(
            "<li class=\"card rank-" + rank + " " + suit + "\">" +
            "<span class=\"rank\">" + rank + "</span>" +
            "<span class=\"suit\">&" + suit + ";</span>" +
            "</li>"
        );
    });
}

function startMatch() {
    stompClient.send("/app/start-match", {}, JSON.stringify({'matchName': $("#match-name").val()}));
}

// Listeners

$(function () {
    $("form").on('submit', function (e) {
        e.preventDefault();
    });

    $("#connect").click(function () {
        connect();
    });
    $("#disconnect").click(function () {
        disconnect();
    });

    $("#create-match").click(function () {
        createMatch();
    });
    $("#join-match").click(function () {
        var joinRequest = {
            'matchName': $("#match-name").val(),
            'playerName': $("#player-name").val()
        };
        joinMatch(joinRequest);
    });
    $("#start-match").click(function () {
        startMatch();
    });
});
