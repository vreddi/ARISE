$(document).ready(function() {
	$('#service li').on('click', function(event) {
        getAspects();
});

function getAspects(){

	$.ajax({
        url: "php/getUploadInfo.php?query=getAspects",
        type: "GET",
        async: false,
        success: function (response) {
            aspectJSON = JSON.parse(response);
            console.log(aspectJSON);

            // Remove all the content
            $('table').remove();
            addAspectContentToTable(aspectJSON);
        }
    });
}

function addAspectContentToTable(aspectJSON){

    html = '';

    html += '<table class="table table-hover" style="margin-left: 300px;">';
    html += '<thead>';
    html += '   <tr>';
    html += '       <th>Name</th>';
    html += '       <th>Description</th>';
    html += '       <th>Download</th>';
    html += '   </tr>';
    html += '</thead>';

    html += '<tbody>';

    console.log(Object.keys(aspectJSON));
    Object.keys(aspectJSON).forEach(function (key) {
        
        html += ' <tr>';
        html += '       <td>' + aspectJSON[key]['Name'] + '</td>';
        html += '       <td>' + aspectJSON[key]['Description']+ '</td>';
        html += '       <td><a href=\'php/' + aspectJSON[key]['Location'] + '\'>' + aspectJSON[key]['Location'] + '</a></td>';
        html += ' </tr>';
    });

    html += '</tbody>';

    html += '</table>';

    $('body').append(html);
}

});