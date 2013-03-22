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