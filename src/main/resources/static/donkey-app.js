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
        var selectCardRequest = {
            'matchName': $("#match-name").val(),
            'playerName': $("#player-name").val(),
            'cardIndex': 0
        };
        selectCard(selectCardRequest)
    });
    $("#cards").on('click', '#card-1', function () {
        var selectCardRequest = {
            'matchName': $("#match-name").val(),
            'playerName': $("#player-name").val(),
            'cardIndex': 1
        };
        selectCard(selectCardRequest)
    });
    $("#cards").on('click', '#card-2', function () {
        var selectCardRequest = {
            'matchName': $("#match-name").val(),
            'playerName': $("#player-name").val(),
            'cardIndex': 2
        };
        selectCard(selectCardRequest)
    });
    $("#cards").on('click', '#card-3', function () {
        var selectCardRequest = {
            'matchName': $("#match-name").val(),
            'playerName': $("#player-name").val(),
            'cardIndex': 3
        };
        selectCard(selectCardRequest)
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
        console.log("subscribe to [\" + matchDestination + \"]");
        stompClient.subscribe(matchDestination,
            function (joinResponse) {
                players.push(
                    {
                        'playerName': JSON.parse(joinResponse.body).playerName,
                        'donkeyLevel': ""
                    }
                );
                renderPlayers(players);
            });


        var playerDestination = matchDestination + '/player/' + request.playerName;
        console.log("subscribe to [\" + playerDestination + \"]");
        stompClient.subscribe(playerDestination,
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

function selectCard(request) {
    console.log("iam here");
    stompClient.send("/app/select-card", {}, JSON.stringify(request));
}
