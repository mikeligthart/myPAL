console.log("start loading timeline");
google.load("visualization", "1", {packages:["timeline"]});
var drawingObject = new DrawMyData();

$.getJSON('/mypal/activity/list', function(jsonActivities) {
    $.getJSON('/mypal/measurement/list', function(jsonMeasurements) {
        drawingObject.draw(jsonActivities, jsonMeasurements);
    });
});

function DrawMyData(){
    this.draw = function(activities, measurements){
        var activityCount = Object.keys(activities).length
        google.setOnLoadCallback(drawChart);

        function drawChart() {
                var container = document.getElementById('timeline');
                this.chart = new google.visualization.Timeline(container);
                var dataTable = new google.visualization.DataTable();

                dataTable.addColumn({ type: 'string', id: 'DiaryItem' });
                dataTable.addColumn({ type: 'string', id: 'Type' });
                dataTable.addColumn({ type: 'date', id: 'Start' });
                dataTable.addColumn({ type: 'date', id: 'End' });

                var lastIndexActivities = dataTable.addRows(getActivities(activities));
                var lastIndexMeasurements = dataTable.addRows(getMeasurements(measurements));
                console.log("Activities: " + lastIndexActivities + ", Measurements:" + lastIndexMeasurements);



                var options = {
                    'tooltip' : {trigger: 'none'},
                    colors: setColors(activities, measurements)
                };

                function selectHandler() {
                        var selection = chart.getSelection()[0];
                        if (selection) {
                            var index = selection.row;
                            var selectedItem;
                            if(index < activityCount){
                                selectedItem = activities[index];

                            } else {
                                index = index - activityCount;
                                selectedItem = measurements[index];
                            }
                            window.location.href = selectedItem.viewURL;

                        }
                }

                function getActivities(activities){
                    var rows = [];
                    for(i = 0; i < activities.length; i++) {
                        var activity = activities[i]
                        var row = [ 'Activiteit', activity.type, new Date(0,0,0,activity.startHour,activity.startMin,0), new Date(0,0,0,activity.endHour,activity.endMin,0)];
                        rows.push(row);
                    }
                    return rows;
                }

                function getMeasurements(measurements){
                    var rows = [];
                    for(i = 0; i < measurements.length; i++) {
                        var measurement = measurements[i]
                        var row = [ 'Meting', measurement.type, new Date(0,0,0,measurement.startHour,measurement.startMin,0), new Date(0,0,0,measurement.endHour,measurement.endMin,0)];
                        rows.push(row);
                    }
                    return rows;
                }

                function setColors(activities, measurements){
                    var color = [];
                    for(i = 0; i < activities.length; i++) {
                        var activity = activities[i];
                        color.push(activity.color);
                    }

                    for(i = 0; i < measurements.length; i++) {
                        var measurement = measurements[i];
                        color.push(measurement.color);
                    }
                    console.log("colors: " + color);
                    return color;
                }

                // Listen for the 'select' event, and call my function selectHandler() when
                // the user selects something on the chart.
                google.visualization.events.addListener(chart, 'select', selectHandler);
                chart.draw(dataTable, options);
        }
    }

}