google.load("visualization", "1", {packages:["timeline"]});
var drawingObject = new DrawMyData();

$.getJSON('/mypal/activity/list', function(jsonActivities) {
    drawingObject.draw(jsonActivities);
});

function DrawMyData(){
    this.draw = function(data){
        google.setOnLoadCallback(drawChart);

        function drawChart() {
                var container = document.getElementById('timeline');
                this.chart = new google.visualization.Timeline(container);
                var dataTable = new google.visualization.DataTable();

                dataTable.addColumn({ type: 'string', id: 'DiaryItem' });
                dataTable.addColumn({ type: 'string', id: 'Type' });
                dataTable.addColumn({ type: 'date', id: 'Start' });
                dataTable.addColumn({ type: 'date', id: 'End' });

                dataTable.addRows(getRows(data));

                var options = {
                    'tooltip' : {trigger: 'none'},
                    colors: setColors(data)
                };

                function selectHandler() {
                        var selectedItem = chart.getSelection()[0];
                        if (selectedItem) {
                            var selectedActivity = data[selectedItem.row];
                            window.location.href = selectedActivity.viewURL;
                        }
                }

                function getRows(activities){
                    var rows = [];
                    for(i = 0; i < activities.length; i++) {
                        var activity = activities[i]
                        var row = [ 'Activiteit', activity.type, new Date(0,0,0,activity.startHour,activity.startMin,0), new Date(0,0,0,activity.endHour,activity.endMin,0)];
                        rows.push(row);
                    }
                    return rows;
                }

                function setColors(activities){
                    var color = [];
                    for(i = 0; i < activities.length; i++) {
                        var activity = activities[i];
                        color.push(activity.color);
                    }
                    return color;
                }

                // Listen for the 'select' event, and call my function selectHandler() when
                // the user selects something on the chart.
                google.visualization.events.addListener(chart, 'select', selectHandler);
                chart.draw(dataTable, options);
        }
    }

}