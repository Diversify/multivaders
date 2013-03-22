var socket = new WebSocket("ws://localhost:9000/");

socket.onopen = function () {
    console.log("Socket has been opened, spawning evil...");

    socket.onmessage = function (msg) {
        console.log('Got message:')
        console.log(msg);
    }


}

function send(text) {
    try {
        socket.send(text);
        console.log('Sent: ' + text);
    } catch (exception) {
        console.log('Error: ' + exception);
    }
}

$('#send-button').on('click', function(e){
    send($('#text').val());
});