console.log("hello world")

let sessionId;
let websocket;

window.onbeforeunload = function() {
    websocket.onclose = function () {}; // disable onclose handler first
    websocket.close();
};

function siteLoaded(localSessionId, websocketUrl) {
    console.log("Initializing for sessionId " + localSessionId)
    sessionId = localSessionId

    startWebsocket(websocketUrl)
}

function startWebsocket(websocketUrl) {
    websocket = new WebSocket(websocketUrl + sessionId);

    websocket.onopen = function (event) {
        console.log("socket connection opened!")
    };

    websocket.onmessage = function (event) {
        console.log("new message on socket! ")
        console.log(event)

    };

    websocket.onclose = function (event) {
        console.log("socket connection closed!")
    };
}

function sendMessage(message) {
    websocket.send(message);
}
