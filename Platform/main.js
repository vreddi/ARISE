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
            peopleJSON = JSON.parse(response);
            console.log(peopleJSON);
        }
    });
}
});