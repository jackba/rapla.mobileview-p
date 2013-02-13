/* Copyright (C) 2011  Robert Hoppe */
// disable tapTogle globally for all devices to show always the header
$("[data-role=header]").fixedtoolbar({ tapToggle: false });

// special resize function to handle scrollview and orientation changes
function ResizePageContentHeight(page, wait) {
	if(page.id == "popup") return;
	if (uagent.search("windows phone os 7") > -1) return;
	setTimeout(function() {
		var $page = $(page.target),
		$content = $page.children(".ui-content"),
		hh = $page.children(".ui-header").outerHeight() || 35,
		fh = $page.children(".ui-footer").outerHeight() || 40,
		fh = 0;
		pt = parseFloat($content.css("padding-top")) || 0,
		pb = parseFloat($content.css("padding-bottom")) || 0,
		wh = window.innerHeight || 0;
		toResize = wh - hh - (pt + pb);
		if (toResize < 100 && page.orientation == "lanscape") toResize = 185;
		if (toResize < 300 && page.orientation == "potrait") toResize = 430;
		$(".bufRow").height(fh);
		$(".view_container").height(toResize);
	}, wait);
}

// create the horizontal scrollable week view table
function parseShow(theis,event, wait)
{
	var  $page = $(theis);
	// no data-transition effects with android - flickering on jqm 1.0.1
	if (uagent.search("android") > -1) $("a").attr("data-transition", "none");
	// scrollview is not necessary for horizontal scroll on windows phone 7
	if (uagent.search("windows phone os 7") > -1) {
		$("#calendar").css("overflow", "auto");
		$("#calendar .view_container").css("overflow", "auto");
	} else {
		// initiate scroll view only on non-popups
		if (event.target.id != "popup") $page.find(".ui-content").attr( "data-" + $.mobile.ns + "scroll", "y");
		$page.find(":jqmData(scroll):not(.ui-scrollview-clip)").each(function () {
			var $this = $( this );
			if ( $this.hasClass( "ui-scrolllistview" ) ) {
				$this.scrolllistview();
			} else {
				var st = $this.jqmData( "scroll" ) + "",
					paging = st && st.search(/^[xy]p$/) != -1,
					dir = st && st.search(/^[xy]/) != -1 ? st.charAt(0) : null,
					opts = {
						direction: dir || undefined,
						paging: paging || undefined,
						scrollMethod: $this.jqmData("scroll-method") || undefined
					};
				$this.scrollview(opts);
			}
		});
	}
	// resize with wait to get the right size (for support in android)
	ResizePageContentHeight(event, wait);
}

$(":jqmData(role='page')").live("pageshow", function(event) {
	var wait = (uagent.search("android") > -1)? 250 : 50;
	parseShow(this, event, wait);
	if (uagent.search("android") > -1) window.scrollTo(0, 0);
	// scroll to first element - uncommented, bad support on android and wp7
	/*
	if ($("#firstEvent").length > 0) {
		var $first = $("#firstEvent");
		if ($first.length == 1) {
			var eventPos = $first.position();
			//$('.view_container').scrollview("scrollTo", -eventPos.left + 5, -eventPos.top + 5);
			//$this.scrollview("scrollTo", 100, 100);
		}
	}
	*/
	// disable tapTogle for wp7 and android for compatibility reasons
	if (uagent.search("windows phone os 7") > -1 || uagent.search("android") > -1) {
		$("[data-role=footer]").fixedtoolbar({ tapToggle: false });
	}
});

// resize on orientationchange to fix scrollview
$(window).bind( "orientationchange", function(event){
	$.mobile.silentScroll(1);
	var wait = (uagent.search("android") > -1)? 250 : 100;
	parseShow(this, event, wait);
	$.mobile.silentScroll(1);
});

// set default dateFormat for datebox
jQuery.extend(jQuery.mobile.datebox.prototype.options, { 
	'useDialogForceTrue': true,
	'useDialogForceFalse': false,
	'useDialog': true 
});

// parseDate for the datepicker dialog
function parseDate(input, format) {
	format = format || 'yyyy-mm-dd';
	var parts = input.match(/(\d+)/g), 
		i = 0, fmt = {};
	format.replace(/(yyyy|dd|mm)/g, function(part) { fmt[part] = i++; });
	return new Date(parts[fmt['yyyy']], parts[fmt['mm']]-1, parts[fmt['dd']]);
}

// submit with parsed date in order to support rapla url format
$("#pickdate").live("pagecreate", function(event){
	$("#mydate").click(function(){
		$(this).datebox('open');
	});
	$("#pickDateForm").submit(function(){
		var dateVal = $("#mydate").val();
		var curUrl = $("#curUrl").val();
    	var dt = parseDate(dateVal);
    	// catch non numbers
		if (isNaN(dt)) {
			return false;
		} else {
			// set the right date url
			var urlDate = "day=" + dt.getDate() + "&month=" + (dt.getMonth() + 1) + "&year=" + dt.getFullYear();
			$.mobile.changePage(curUrl + urlDate);
			return false;
		}
	});
});
