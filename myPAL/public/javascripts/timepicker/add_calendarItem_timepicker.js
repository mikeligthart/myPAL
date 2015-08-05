var lang = Messages('javascript.timepicker.lang');

$('#starttime').timepicker({'timeFormat': 'H:i',
                            'minTime': '00:00',
                            'maxTime': '23:59',
                            'step': 15,
                            'scrollDefault': 'now'
});

$('#endtime').timepicker({  'timeFormat': 'H:i',
                            'minTime': '00:00',
                            'maxTime': '23:59',
                            'step': 15,
                            'lang': lang,
                            'scrollDefault': 'now'
});

$('#starttime').on('changeTime', function(){
    var mintime = $('#starttime').timepicker('getTime');
    $('#endtime').timepicker('option', { 'minTime': mintime, 'showDuration': true });


});