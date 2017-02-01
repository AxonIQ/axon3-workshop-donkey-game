var stompClient = null;

function connect() {
    var socket = new SockJS('/donkey-websocket');
    stompClient = Stomp.over(socket);
    stompClient.connect({}, function (frame) {
        setConnected(true);
        console.log('Connected: ' + frame);
        stompClient.subscribe('/topic/matches', function (match) {
            updateMatches(JSON.parse(match.body));
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

function createMatch() {
    stompClient.send("/app/create-match", {}, JSON.stringify({'matchName': $("#match-name").val()}));
}

function joinMatch(joinRequest) {
    stompClient.send("/app/join-match", {}, JSON.stringify(joinRequest));

    stompClient.subscribe('/topic/match/' + joinRequest.matchName, function (match) {
        updateMatches(JSON.parse(match.body));
    });
}

function startMatch(matchName) {
    stompClient.send("/app/start-match", {}, JSON.stringify({'matchName': $(matchName).val()}));
}

function updateMatches(match) {
    $("#matches").append("<tr><td>" + match.matchName + "</td></tr>");
}

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
            'matchName' : $("#join-match-with-name").val(),
            'userName' : $("#user-name").val()
        };
        joinMatch(joinRequest);
    });
});
