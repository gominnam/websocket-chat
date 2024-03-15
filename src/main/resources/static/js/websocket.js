

let stompClient = null;
let isConnecting = false;

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
    if(isConnecting || !connectValidation()) return;
    isConnecting = true;
    const token = localStorage.getItem('accessToken');
    if(!token){
        alert("Please login first");
        isConnecting = false;
        return;
    }
    const socket = new SockJS("/websocket-chat");
    let options = {debug: false};

    stompClient = Stomp.over(socket, options);

    let headers = {Authorization: "Bearer " + token};
    console.log("headers: ", headers)
    stompClient.connect(headers, function (frame) {
        setConnected(true);
        console.log('Connected: ' + frame);
        stompClient.subscribe('/topic/greetings', function (greeting) {
            showMessage(JSON.parse(greeting.body));
        });
        joinRoom();
        isConnecting = false;
    }, function(error) {
        alert("Failed to connect to the WebSocket server. Please try again later.");
        console.error('WebSocket connection error: ', error);
        isConnecting = false;
    });
}

function disconnect() {
    if (stompClient !== null) {
        exitRoom();
        stompClient.disconnect();
    }
    setConnected(false);
    isConnecting = false;
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
    if($("#name").val() === "") {
        alert("Please Login first");
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

document.addEventListener('DOMContentLoaded', () => {
    const token = localStorage.getItem('accessToken');
    if (token) {
        const user = getParsedToken(token);
        if (user) {
            document.getElementById('name').value = user.name;
        }
    }
});
