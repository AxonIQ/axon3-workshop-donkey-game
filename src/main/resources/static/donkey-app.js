var stompClient = null;
var alert_id = 0;
var dismiss_id = 0;

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
            'matchName': getMatchName(),
            'playerName': getPlayerName()
        };
        joinMatch(joinRequest);
        disableInputFields();
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
    alert_id++;
    var template = "<div id='" + alert_id + "' class='alert alert-success alert-dismissible' role='alert'><button type='button' class='close' data-dismiss='alert' aria-label='Close'><span aria-hidden='true'>&times;</span></button>";
    template = template + "[" + alert_id + "] " + response + "</div>"

    $("#alert-container").append(template);
    window.setTimeout(function () {
        dismiss_id++;
        $("#" + dismiss_id).fadeTo(500, 0).slideUp(500, function () {
            $(this).remove();
        });
    }, 4000);
}

function disableInputFields() {
    $("#match-name").prop("disabled", true);
    $("#player-name").prop("disabled", true);
    $("#create-match").prop("disabled", true);
    $("#join-match").prop("disabled", true);
}

function disableStartMatchButton() {
    $("#start-match").prop("disabled", true);
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

var players = [];

var hand = null;
var joinedMatch = false;

function createMatch() {
    stompClient.send("/app/create-match", {}, JSON.stringify({'matchName': getMatchName()}));
}

function joinMatch(request) {
    stompClient.send("/app/join-match", {}, JSON.stringify(request));

    if (!joinedMatch) {
        var matchDestination = '/topic/match/' + request.matchName;
        console.log("subscribe to [" + matchDestination + "]");
        stompClient.subscribe(matchDestination,
            function (matchResponse) {
                var responseBody = JSON.parse(matchResponse.body);
                players = responseBody.players;
                renderPlayers(players);
            });


        var playerDestination = matchDestination + '/player/' + request.playerName;
        console.log("subscribe to [" + playerDestination + "]");
        stompClient.subscribe(playerDestination,
            function (gameResponse) {
                var responseBody = JSON.parse(gameResponse.body);
                if ('canFinish' in responseBody) {
                    var canFinish = responseBody.canFinish;
                    $("#call-finish-button").prop("disabled", !canFinish);
                } else if ('outcome' in responseBody) {
                    renderOutcome(responseBody.outcome);
                } else if ('hand' in responseBody) {
                    hand = responseBody.hand;
                    disableStartMatchButton();
                } else if ('card' in responseBody) {
                    hand.push(responseBody.card);
                }
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
            "<td>" + player + "</td>" +
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

function renderOutcome(outcome) {
    if (outcome) {
        $("#outcome").append("<img class=\"outcome-image\" src=\"winner.png\">");
    } else {
        $("#outcome").append("<img class=\"outcome-image\" src=\"loser.png\">");
    }
}

function startMatch() {
    stompClient.send("/app/start-match", {}, JSON.stringify({'matchName': getMatchName()}));
}

function selectCard(cardIndex) {
    //Remove selected card
    hand.splice(cardIndex, 1);
    renderHand(hand);

    //Send select request
    var request = {
        'matchName': getMatchName(),
        'playerName': getPlayerName(),
        'cardIndex': cardIndex
    };
    stompClient.send("/app/select-card", {}, JSON.stringify(request));
}

function getMatchName() {
    return $("#match-name").val().split(' ').join('');
}

function getPlayerName() {
    return $("#player-name").val().split(' ').join('');
}