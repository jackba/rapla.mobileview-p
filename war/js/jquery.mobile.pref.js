var uagent = "";
if (navigator && navigator.userAgent) uagent = navigator.userAgent.toLowerCase();

$(document).ready(function(){
	if (uagent.search("windows phone os 7") > -1) $("*[data-position=\"fixed\"]").removeAttr("data-position");
});

$(document).bind("mobileinit", function(){
	if (uagent.search("android") > -1) {
		$.mobile.defaultPageTransition = 'none';
	    $.mobile.defaultDialogTransition = 'none';
	    $.mobile.useFastClick = true;
	}
});

