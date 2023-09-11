var stompClient = null;

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

function connect() {
    if(!connectValidation()) return;
    var socket = new SockJS('/other-country-chat');
    stompClient = Stomp.over(socket);
    stompClient.connect({}, function (frame) {
        setConnected(true);
        sendName(true);
        console.log('Connected: ' + frame);
        stompClient.subscribe('/topic/greetings', function (greeting) {
            showMessage(JSON.parse(greeting.body));
        });
    });

}

function disconnect() {
    if (stompClient !== null) {
        sendName(false);
        stompClient.disconnect();
    }
    setConnected(false);
    console.log("Disconnected");
}

function sendName(connected) {
    if(connected)
        stompClient.send("/app/hello", {}, JSON.stringify({'name': $("#name").val()+"님이 입장하셨습니다."}));
    else if(!connected)
        stompClient.send("/app/hello", {}, JSON.stringify({'name': $("#name").val()+"님이 퇴장하셨습니다."}));
}

function sendMessage(){
    if($("#message").val() == null) return;
    stompClient.send("/app/message", {}, JSON.stringify({'message': $("#message").val()}));
    $("#message").val("");
}

function showMessage(message) {
    let recieveMessage = message.message;
    if(recieveMessage == null)
        recieveMessage = message.name;
    $("#greetings").append("<tr><td>" + recieveMessage + "</td></tr>");
}

function connectValidation() {
    if($("#name").val() == "") {
        alert("Please enter your nickname");
        return false;
    }
    return true;
}


$(function () {
    $("form").on('submit', function (e) {
        e.preventDefault();
    });
    $( "#connect" ).click(function() { connect(); });
    $( "#disconnect" ).click(function() { disconnect(); });
    $( "#send" ).click(function() { sendMessage(); });
});
