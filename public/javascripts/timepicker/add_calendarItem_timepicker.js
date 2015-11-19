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
                            'lang': {mins: 'min', hr: 'uur', hrs: 'uren'},
                            'scrollDefault': 'now'
});

$('#starttime').on('selectTime', function(){
    var mintime = $('#starttime').timepicker('getTime');
    $('#endtime').timepicker('option', { 'minTime': mintime, 'showDuration': true });
    if(mintime > $('#endtime').timepicker('getTime')){
        $('#endtime').timepicker('setTime', mintime);
    }
});

$('#endtime').on('selectTime', function(){
    var maxtime = $('#endtime').timepicker('getTime');
    $('#starttime').timepicker('option', 'maxTime', maxtime);
    if(maxtime < $('#starttime').timepicker('getTime')){
         $('#starttime').timepicker('setTime', maxtime);
    }
});

$('#date').datepicker();

