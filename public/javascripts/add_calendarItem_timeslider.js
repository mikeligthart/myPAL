(function($)
{
	/*
	$('.timepick').change(function()
	{
		$('#chosen-time').text($(this).timeslider('get').toString());
	}).timeslider({value: '12:00'});

	$('#toggler').click(function(e)
	{
		e.preventDefault();
		$('.timepick,.timepick2').timeslider('toggle').addClass('chain-ok');
		$('#chain-test').text($('.timepick,.timepick2').not('.chain-ok').size() > 0 ? 'chain test failed' : 'chain test passed');
	});

	$('.timepick2').timeslider({ showValue: false, clickable: true });
	*/

	$('#starttime').timeslider({ showValue: true, clickable: true });
	$('#endtime').timeslider({ showValue: true, clickable: true });

    $('#starttime').change(function()
    {
        var startDate = $(this).timeslider('get');
        var endDate = $('#endtime').timeslider('get');
        if(endDate.getTime() < new Date(startDate.getTime() + 15*60000).getTime()){
            var time;
            if(startDate.getHours() >= 23 && startDate.getMinutes() >= 29){
                time = startDate;
            } else {
                time = new Date(startDate.getTime() + 15*60000);
            }
            $('#endtime').timeslider('set', time);
        }
    });

    $('#endtime').change(function()
        {
            var endDate = $(this).timeslider('get');
            var startDate = $('#starttime').timeslider('get');
            if(endDate.getTime() < new Date(startDate.getTime() + 15*60000).getTime()){
                var time;
                if(startDate.getHours() <= 0 && startDate.getMinutes() <= 16){
                    time = endDate;
                } else {
                    time = new Date(endDate.getTime() - 15*60000);
                }
                $('#starttime').timeslider('set', time);
            }
        });

})(jQuery);

$('#date').datepicker();