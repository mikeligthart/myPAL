function setDayPart(time){
    var sober = Date.parse("00:00");
    var afterbreakfast = Date.parse("07:30");
    var beforelunch = Date.parse("11:00");
    var afterlunch = Date.parse("12:30");
    var beforedinner = Date.parse("16:00");
    var afterdinner = Date.parse("17:30");

    if (time.between(sober, afterbreakfast)){
        $("#daypart").val("SOBER");
    } else if (time.between(afterbreakfast, beforelunch)){
        $("#daypart").val("AFTERBREAKFAST");
    } else if (time.between(beforelunch, afterlunch)){
        $("#daypart").val("BEFORELUNCH");
    } else if (time.between(afterlunch, beforedinner)){
        $("#daypart").val("AFTERLUNCH");
    } else if (time.between(beforedinner, afterdinner)){
        $("#daypart").val("BEFOREDINNER");
    } else {
        $("#daypart").val("AFTERDINNER");
    }
}

$('#starttime').timepicker({'timeFormat': 'H:i',
                            'minTime': '00:00',
                            'maxTime': '23:59',
                            'step': 15,
                            'scrollDefault': 'now'
});


$('#starttime').on('selectTime', function(){
    var time = $('#starttime').timepicker('getTime');
    var newTime = new Date(time.getTime() + 300000);
    document.getElementById("endtime").value = newTime.toString("HH:mm");
    setDayPart(time);
});

$('#date').datepicker();