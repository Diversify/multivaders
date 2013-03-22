var communication = (function(){

    function init( callback ){
        var socket = new WebSocket("ws://localhost:9000/");
        socket.onopen = function () {
            console.log("Socket has been opened, spawning evil...");
            socket.onmessage = function (msg) {
                console.log('Got message:')
                console.log(msg);
            }
            callback();
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


    return {
        init: init,
        send: send
    }
})();

communication.init(function(){
    $('#send-button').on('click', function(e){
        communication.send($('#text').val());
    });
});

