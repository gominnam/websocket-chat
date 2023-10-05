var stompClient = null;

function setConnected(connected) {
    $("#connect").prop("disabled", connected);
    $("#disconnect").prop("disabled", !connected);
    if (connected) {
        $("#conversation").show();
    } else {
        $("#conversation").hide();
    }
    $("#greetings").html("");
}

function connect() {
    if(!connectValidation()) return;
    var socket = new SockJS('/websocket-chat');
    stompClient = Stomp.over(socket);
    stompClient.connect({}, function (frame) {
        setConnected(true);
        console.log('Connected: ' + frame);
        stompClient.subscribe('/topic/greetings', function (greeting) {
            showMessage(JSON.parse(greeting.body));
        });
        joinRoom();
    });
}

function disconnect() {
    if (stompClient !== null) {
        exitRoom();
        stompClient.disconnect();
    }
    setConnected(false);
    console.log("Disconnected");
}

function joinRoom(){
    stompClient.send("/app/entry", {}, JSON.stringify({'sender': $("#name").val(), 'content': '님이 입장하셨습니다.'}));
}

function exitRoom(){
    stompClient.send("/app/entry", {}, JSON.stringify({'sender': $("#name").val(), 'content': '님이 퇴장하셨습니다.'}));
}

function sendMessage(){
    if($("#content").val() == null) return;
    stompClient.send("/app/message", {}, JSON.stringify({'sender': $("#name").val(), 'content': $("#content").val()}));
    $("#content").val("");
}

function showMessage(message) {
    $("#greetings").append("<tr><td>" + message.sender + message.content + "</td></tr>");
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
