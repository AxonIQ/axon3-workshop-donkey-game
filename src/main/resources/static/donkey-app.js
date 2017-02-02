var stompClient = null;

// Infra

$(function () {
    connect();

    $("form").on('submit', function (e) {
        e.preventDefault();
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


    $("#cards").on('click', '#card-0', function () {
        selectCardIfPossible(0);
    });
    $("#cards").on('click', '#card-1', function () {
        selectCardIfPossible(1);
    });
    $("#cards").on('click', '#card-2', function () {
        selectCardIfPossible(2);
    });
    $("#cards").on('click', '#card-3', function () {
        selectCardIfPossible(3);
    })

});

function connect() {
    var socket = new SockJS('/donkey-websocket');
    stompClient = Stomp.over(socket);
    stompClient.connect({}, function (frame) {
        console.log('Connected: ' + frame);

        console.log('subscribe to [/topic/alerts]');
        stompClient.subscribe('/topic/alerts', function (alertResponse) {
            var alert = JSON.parse(alertResponse.body);
            sendAlert(alert.success, alert.response);
        });
    });
}

function sendAlert(success, response) {
    alert(response);
}

function selectCardIfPossible(cardIndex) {
    if (alreadySelectedACard()) {
        sendAlert(false, "You've already selected a card")
    } else {
        selectCard(cardIndex);
    }
}

function alreadySelectedACard() {
    return hand.length == 3;
}

// Game

var players = [];
var hand = null;
var joinedMatch = false;

function createMatch() {
    stompClient.send("/app/create-match", {}, JSON.stringify({'matchName': $("#match-name").val()}));
}

function joinMatch(request) {
    stompClient.send("/app/join-match", {}, JSON.stringify(request));

    if (!joinedMatch) {
        var matchDestination = '/topic/match/' + request.matchName;
        console.log("subscribe to [" + matchDestination + "]");
        stompClient.subscribe(matchDestination,
            function (joinResponse) {
                replacePlayers(JSON.parse(joinResponse.body).players);
                renderPlayers(players);
            });


        var playerDestination = matchDestination + '/player/' + request.playerName;
        console.log("subscribe to [" + playerDestination + "]");
        stompClient.subscribe(playerDestination,
            function (cardsResponse) {
                var responseBody = JSON.parse(cardsResponse.body);
                if ('hand' in responseBody) {
                    hand = responseBody.hand;
                } else if ('card' in responseBody) {
                    hand.push(responseBody.card);
                }
                renderHand(hand);
            });

        joinedMatch = true;
    }

}

function replacePlayers(playersList) {
    players = [];
    $.each(playersList, function (key, playerName) {
        players.push(
            {
                'playerName': playerName,
                'donkeyLevel': ""
            }
        );
    });
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
    // Remove previous hand
    for (var i = 0; i < 4; i++) {
        $("#card-" + i).remove();
    }

    // Render current hand
    $.each(hand, function (key, card) {
        var rank = card.rank.letter;
        var suit = card.suit.toLowerCase();
        $("#cards").append(
            "<li><a id=\"card-" + key + "\" class=\"card rank-" + rank + " " + suit + "\" href=\"#\">" +
            "<span class=\"rank\">" + rank + "</span>" +
            "<span class=\"suit\">&" + suit + ";</span>" +
            "</a></li>"
        );
    });
}

function startMatch() {
    stompClient.send("/app/start-match", {}, JSON.stringify({'matchName': $("#match-name").val()}));
}

function selectCard(cardIndex) {
    //Remove selected card
    hand.splice(cardIndex, 1);
    renderHand(hand);

    //Send select request
    var request = {
        'matchName': $("#match-name").val(),
        'playerName': $("#player-name").val(),
        'cardIndex': cardIndex
    };
    stompClient.send("/app/select-card", {}, JSON.stringify(request));
}
