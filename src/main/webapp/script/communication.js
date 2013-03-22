var comlink = (function(){
    var socket,
        url = window.location.origin.match(/[^http://].+[^\d]/);

    function init( callback , messageHandler){
        socket = new WebSocket("ws://" + url + "8090/");
        socket.onopen = function () {
            console.log("Socket has been opened, spawning evil...");
            socket.onmessage = messageHandler;
//                function (msg) {
//                    console.log('Got message:')
//                    console.log(msg);
//                }
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

comlink.init(function(){
//    $('#send-button').on('click', function(e){
//        comlink.send($('#text').val());
//    });
});

