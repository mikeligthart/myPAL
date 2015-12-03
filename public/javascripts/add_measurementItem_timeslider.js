(function($)
{
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

    $('#starttime').timeslider({ showValue: true, clickable: true });

    $('#starttime').change(function()
    {
        var startDate = $(this).timeslider('get');
        var time = startDate.getTime();
        var newTime = new Date(time + 300000);
        document.getElementById("endtime").value = newTime.toString("HH:mm");
        setDayPart(new Date(time));
    });

})(jQuery);

$('#date').datepicker();