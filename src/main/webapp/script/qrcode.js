(function($) {

    var url = window.location.origin;

    $('.qr-code').qrcode({
        text: url,
        width: 100,
        height: 100
    });

})(jQuery)