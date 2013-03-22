$('#left').on('click', function(){
    comlink.send('ld');
    comlink.send('lu');
});

$('#right').on('click', function(){
    comlink.send('rd');
    comlink.send('ru');
});

$('#shoot').on('click', function(){
    comlink.send('sd');
    comlink.send('su');
});


document.onkeypress = function (e) {
    //define the Z key
    this.Z_KEY = 122;
    //define the X key
    this.X_KEY = 120;
    //define the space key
    this.SPACE_KEY = 32;

    this.LEFT_PRESS_CODE = 'lp';
    this.RIGHT_PRESS_CODE = 'rp';
    this.SHOOT_PRESS_CODE = 'sp';

    this.LEFT_DOWN_CODE = 'ld';
    this.RIGHT_DOWN_CODE = 'rd';
    this.SHOOT_DOWN_CODE = 'sd';

    this.LEFT_UP_CODE = 'lu';
    this.RIGHT_UP_CODE = 'ru';
    this.SHOOT_UP_CODE = 'su';


    //get the event
    var e = window.event || e;
    //char code
    var charCode = e.charCode;

    switch (charCode) {
        case this.Z_KEY:
//                comlink.send(this.LEFT_PRESS_CODE);
            comlink.send(this.LEFT_DOWN_CODE);
            comlink.send(this.LEFT_UP_CODE);
//                player.moveLeft();
            break;
        case this.X_KEY:
//                comlink.send(this.RIGHT_PRESS_CODE);
            comlink.send(this.RIGHT_DOWN_CODE);
            comlink.send(this.RIGHT_UP_CODE);
//                player.moveRight();
            break;
        case this.SPACE_KEY:
//                comlink.send(this.SHOOT_PRESS_CODE);
            comlink.send(this.SHOOT_DOWN_CODE);
            comlink.send(this.SHOOT_UP_CODE);
//                lasers[lasers.length] = new Laser(player);
            break;
    }
}
