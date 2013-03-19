/*--------------------------------------------------------------------------*
 | Copyright (C) 2011 Robert Hoppe		                                    |
 |                                                                          |
 | This program is free software; you can redistribute it and/or modify     |
 | it under the terms of the GNU General Public License as published by the |
 | Free Software Foundation. A copy of the license has been included with   |
 | these distribution in the COPYING file, if not go to www.fsf.org         |
 |                                                                          |
 | As a special exception, you are granted the permissions to link this     |
 | program with every library, which license fulfills the Open Source       |
 | Definition as published by the Open Source Initiative (OSI).             |
 *--------------------------------------------------------------------------*/
package org.rapla.plugin.mobile;

import java.io.IOException;
import java.text.MessageFormat;
import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.rapla.components.calendarview.html.AbstractHTMLView;
import org.rapla.components.calendarview.html.HTMLMobileWeekView;
import org.rapla.components.calendarview.html.HTMLWeekView;
import org.rapla.components.util.SerializableDateTimeFormat;
import org.rapla.entities.configuration.Preferences;
import org.rapla.entities.configuration.RaplaConfiguration;
import org.rapla.entities.domain.Appointment;
import org.rapla.entities.domain.Reservation;
import org.rapla.entities.domain.internal.ReservationImpl;
import org.rapla.entities.storage.RefEntity;
import org.rapla.facade.ClientFacade;
import org.rapla.framework.Configuration;
import org.rapla.framework.ConfigurationException;
import org.rapla.framework.RaplaContext;
import org.rapla.framework.RaplaException;
import org.rapla.gui.CalendarModel;
import org.rapla.gui.CalendarOptions;
import org.rapla.plugin.abstractcalendar.AbstractHTMLCalendarPage;
import org.rapla.plugin.abstractcalendar.GroupAllocatablesStrategy;
import org.rapla.plugin.abstractcalendar.RaplaBuilder;

public class HTMLMobileWeekViewPage extends AbstractHTMLCalendarPage
{
    RaplaBuilder builder;
    String calendarviewHTML;
    Date startDate;
    Date endDate;
    private Configuration config;
    
    public HTMLMobileWeekViewPage(RaplaContext context, CalendarModel calendarModel) throws RaplaException
    {
        super(context, calendarModel);
        setChildBundleName(MobilePlugin.RESOURCE_FILE);
        
        // init the configuration
        initConfiguration();
    }
    
    /**
     * get the current HTML Calendar
     */
    public String getCalendarHTML() {
        return calendarviewHTML;
    }
    
    /**
     * Create the current calendar view
     */
    protected AbstractHTMLView createCalendarView() {
    	HTMLMobileWeekView weekView = new HTMLMobileWeekView(){
            protected String getWeekNumberRow() {
                return MessageFormat.format("("+getString("week") + " {0,date,w})", getStartDate());
            }
        };
        
       return weekView;
    }

    /** overide this for daily views*/
   	protected int getDays(CalendarOptions calendarOptions) {
   		return calendarOptions.getDaysInWeekview();
   	}
    
	protected void configureView() {
		HTMLMobileWeekView weekView = (HTMLMobileWeekView) view;
		CalendarOptions opt = getCalendarOptions();
        weekView.setRowsPerHour( opt.getRowsPerHour() );
        weekView.setWorktime( opt.getWorktimeStart(), opt.getWorktimeEnd() );
        weekView.setShowNonEmptyExcludedDays( true ) ;
        weekView.setFirstWeekday( opt.getFirstDayOfWeek());
        int days = getDays(opt);
		weekView.setDaysInView( days);
		Set<Integer> excludeDays = opt.getExcludeDays();
        if ( days <3)
		{
			excludeDays = new HashSet<Integer>();
		}
        weekView.setExcludeDays( excludeDays );
	}
    
    /**
     * Build the calendar of the current view
     * @throws ConfigurationException 
     */
    protected RaplaBuilder createBuilder(HttpServletRequest request) throws RaplaException {
    	HTMLMobileRaplaBuilder builder = new HTMLMobileRaplaBuilder(getContext());
    	Calendar cal = Calendar.getInstance();
    	cal.setTime(model.getSelectedDate());
    	
    	// set current url for the detail view link of a block
    	builder.setCurUrl(genUrl(request, false) + date2Url(cal));
       
        boolean userColor = false;
        if (config != null) {
        	userColor = config.getAttributeAsBoolean(MobilePlugin.ENABLE_USER_COLOR, false);
        }
        
        // Enable user defined colors if enabled in configuration
        if (userColor) {
        	builder.setUserColor(true);
        }
    	
        builder.setRepeatingVisible(false);
        builder.setExceptionsExcluded(true);
        builder.setFromModel(model, view.getStartDate(), view.getEndDate());

        GroupAllocatablesStrategy strategy = new GroupAllocatablesStrategy( getRaplaLocale().getLocale());
        boolean compactColumns = getCalendarOptions().isCompactColumns() ||  builder.getAllocatables().size() ==0 ;
        strategy.setFixedSlotsEnabled(!compactColumns);
        strategy.setResolveConflictsEnabled(true);
        builder.setBuildStrategy(strategy);

        return builder;
    }
    
    /**
     * Generate the full HTML Output
     */
    public void generatePage(ServletContext context,HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {	
    	// set response for the browsers
        response.setContentType("text/html; charset=" + getRaplaLocale().getCharsetNonUtf());
        java.io.PrintWriter out = response.getWriter();
        
        Calendar calendarview = getRaplaLocale().createCalendar();
        calendarview.setTime(model.getSelectedDate());
        
        // display the current day
        if (request.getParameter("today") != null) {
        	Date currentDay = getQuery().today();
            calendarview.setTime(currentDay);
        // display given date by GET-Parameters
        } else if (request.getParameter("day") != null) {
        	// generate date string from GET-Paramters
        	String dateString = request.getParameter("year") + "-"
                               + request.getParameter("month") + "-"
                               + request.getParameter("day");
            try {
                SerializableDateTimeFormat format = new SerializableDateTimeFormat(getRaplaLocale().createCalendar());
                calendarview.setTime(format.parseDate(dateString, false ));
            } catch (ParseException ex) {
                throw new ServletException( ex);
            }
            
            // display next week
            if (request.getParameter("next") != null) {
                calendarview.add( getIncrementSize(), 1);
            }
                
            // display previous week
            if (request.getParameter("prev") != null) {
                calendarview.add( getIncrementSize(), -1);
            }
        }

        // setup model and view to display the right data
        Date currentDate = calendarview.getTime();
        model.setSelectedDate(currentDate);
        view = createCalendarView();
        view.setLocale(getRaplaLocale().getLocale());
        view.setTimeZone(getRaplaLocale().getTimeZone());
        view.setToDate(model.getSelectedDate());
        model.setStartDate(view.getStartDate());
        model.setEndDate(view.getEndDate());

        try {
            builder = createBuilder(request);
        } catch (RaplaException ex) {
            getLogger().error("Can't create builder ", ex);
            throw new ServletException(ex);
        }
        view.rebuild(builder);
        calendarviewHTML = view.getHtml();

        // START HTML Code
        out.println("<!DOCTYPE html>"); // we have HTML5 
		out.println("<html>");
		
		// generate HTML head
		out.println(generateHTMLHead());

		out.println("<body>");

		// start page container and use different id for popups
		String pageId = "mainpage";
		if (request.getParameter("detail") != null) {
			pageId = "popup";
		}
		out.println("<div data-role=\"page\" id=\"" + pageId + "\">");
		
		// generate header 
		out.println(generateHeader(request, calendarview));
		
		// start content
		out.println("<div data-role=\"content\">");
		
		// show calendar
		if (request.getParameter("detail") == null) {
			out.println("<div id=\"calendar\">");
			out.println("<div class=\"view_container\" id=\"" + dateWeekStamp(calendarview) + "\" data-scroll=\"true\">");
			out.println(getCalendarHTML());
			out.println("</div>");
			out.println("</div>");
		} else {
			out.println("<div id=\"dialog\">");
			// display detail page if appointment and reservation data exists
			try {
				out.println(generateDetailView(request));
			} catch (RaplaException ex) {
				getLogger().error("Cannot create detail view: ", ex);
	            throw new ServletException( ex );
			}
			out.println("</div>");
		}

		// end content
		out.println("</div>");
		
		// generate footer
		out.println(generateFoooter(request));
			
		// end page container
		out.println("</div>");
		
		// dialog page for date picker - dont generate this for detail view in order to save bandwidth
		if (request.getParameter("detail") == null) {
			out.println(generateDatepickDialog(request, calendarview));
		}
		
		// end HTML CODE
		out.println("</body>");
		out.println("</html>");
		
	}
    
    /**
     * Generates the HTML-Head with all CSS and Javascript includes
     * Some settings are made directly with the meta tags
     */
    public String generateHTMLHead()
    {
    	String htmlOut = "";
    	
    	// start head area
    	htmlOut += "<head>\n";
		
		String theTitle;
		// set title to default if there is no title set by the user
		if ((theTitle = getTitle()).isEmpty()) {
			theTitle = getI18n().getString("default_page_title");
		}
		
		htmlOut += "<title>" + theTitle  + "</title>\n";
		
		// include needed javascript files and libraries - every file is document inside the MobilePlugin class
		htmlOut += "<script type=\"text/javascript\" src=\"rapla?page=resource&amp;name=jquery.min.js\"></script>\n";
		
		// this file have to be loaded before jQuery mobile
		htmlOut += "<script type=\"text/javascript\" src=\"rapla?page=resource&amp;name=jquery.mobile.pref.js\"></script>\n"; 
		
		htmlOut += "<script type=\"text/javascript\" src=\"rapla?page=resource&amp;name=jquery.easing.js\"></script>\n";
		htmlOut += "<script type=\"text/javascript\" src=\"rapla?page=resource&amp;name=jquery.mobile.js\"></script>\n";
		htmlOut += "<script type=\"text/javascript\" src=\"rapla?page=resource&amp;name=jquery.mobile.scrollview.js\"></script>\n";
		htmlOut += "<script type=\"text/javascript\" src=\"rapla?page=resource&amp;name=jquery.mobile.datebox.min.js\"></script>\n";
		htmlOut += "<script type=\"text/javascript\" src=\"rapla?page=resource&amp;name=script.js\"></script>\n";

		// include css files
		htmlOut += "<link rel=\"stylesheet\" href=\"rapla?page=resource&amp;name=jquery.mobile.css\" media=\"screen\" type=\"text/css\" />\n";
		htmlOut += "<link rel=\"stylesheet\" href=\"rapla?page=resource&amp;name=mobile.css\" media=\"screen\" type=\"text/css\" />\n";
		
		// tell the html page where its favourite icon is stored
		htmlOut += "<link rel=\"shortcut icon\" type=\"image/x-icon\" href=\"/images/favicon.ico\" />\n";
		
		// meta tags
		htmlOut += "<meta http-equiv=\"Content-Type\" content=\"text/html; charset=" + getRaplaLocale().getCharsetNonUtf() + "\" />\n";
		
		// disable zoom for this web application
		htmlOut += "<meta name=\"viewport\" content=\"width=device-width, minimum-scale=1.0, maximum-scale=1.0\" />\n";
		
		// prevent from beeing indexed by a crawler
		htmlOut += "<meta name=\"robots\" content=\"noindex, nofollow\" />\n";
		
		// tell the iOS based deviced that this site is wep-app-capable in order to hide the 
		htmlOut += "<meta name=\"apple-mobile-web-app-capable\" content=\"yes\" />\n";

		// end head area
		htmlOut += "</head>\n";
		
		return htmlOut;
    }
    
    /**
     * Generates the detail view by given detail-Parameter
     */
    public String generateDetailView(HttpServletRequest request) throws RaplaException
    {
    	// html output
    	String htmlOut = "";
    	
		// only need the reservationId 
    	String showDetail = request.getParameter("detail"); 
		String[] detailIds = showDetail.split("_");
		
		// only 2 results because the detailId-String looks like reservID_appointmID
		if (detailIds.length != 2) {
			throw new RaplaException("The given detail id " + showDetail + " seems to be wrong.");
		}
		
		// parse all Reservations
		Reservation viewReserv = null;
	    for (Reservation reservation:model.getReservations())
	    {
	    	// check for reservation
	    	String reservationId = ((RefEntity) reservation).getId().toString().split("_")[1];
	    	if (reservationId.equals(detailIds[0])) {
	    		viewReserv = reservation;
	    		break;
	    	}
	    }
	    
	    // parse appointments of the specific reservation
	    Appointment viewAppoint = null;
	    if (viewReserv != null) {
	    	Appointment[] Appointments = ((ReservationImpl) viewReserv).getAppointments();
	    	for (Appointment appointment:Appointments) 
	    	{
	    		// check for appointment
	    		String appointmentId = ((RefEntity) appointment).getId().toString().split("_")[1];
		    	if (appointmentId.equals(detailIds[1])) {
		    		viewAppoint = appointment;
		    		break;
		    	}
	    	}
	    	
	    	if (viewAppoint != null) {
	    		// ok we have the right appointment. Let's generate the detail informations of this one.
	    		AppointmentInfoMobile reservationInfo = new AppointmentInfoMobile(getContext());
	    		htmlOut += reservationInfo.getTooltip(viewAppoint);
	    	} else {
	    		htmlOut += htmlError(getI18n().getString("no_detail_found"));
	    	}
	    } else {
	    	htmlOut += htmlError(getI18n().getString("no_detail_found"));
	    }
	    
    	return htmlOut;
    }
    
    /**
     * Generates the date picker dialog that uses the jQuery Mobile Plugin Datebox
     */
    public String generateDatepickDialog(HttpServletRequest request, Calendar calendarview)
    {
    	// Datepicker Default value
    	String currentDate = calendarDateString(calendarview);
    	
    	// HTML Dialog Code
    	String htmlOut = "<div data-role=\"page\" id=\"pickdate\">\n";
    	
    	// Start content
    	htmlOut += "<div data-role=\"content\">\n";
			
    	htmlOut += "<h3>"+ getI18n().getString("set_date") + "</h3>\n";
    	
    	htmlOut += "<form action=\"" + genUrl(request) + "\" method=\"post\" id=\"pickDateForm\">\n";
    	    	
    	// Special Input with settings for the date picker (jQuery Mobile Datebox)
    	htmlOut += "<p><input name=\"mydate\" id=\"mydate\" value=" + currentDate + " type=\"date\" data-role=\"datebox\" data-options='{\"mode\": \"calbox\",\"calWeekMode\": true,\"calWeekModeFirstDay\": 1,\"disableManualInput\": true}' /></p>";
    	
    	htmlOut += "<fieldset class=\"ui-grid-a\">\n";
    	
    	// get back to the last page with this button. Defined href to the mainpage for compatibility reason
    	htmlOut += "<div class=\"ui-block-a\">\n";
    	htmlOut += "<a href=\"#mainpage\" data-direction=\"reverse\" data-role=\"button\" data-rel=\"back\">" + getI18n().getString("btn_back") + "</a>\n";
    	htmlOut += "</div>\n";
    	    	
    	// Display date. This button fires up a Javascript based function that forwards to the right page
    	htmlOut += "<div class=\"ui-block-b\">\n";
    	htmlOut += "<input type=\"submit\" value=\"" + getI18n().getString("btn_show_date") + "\" data-theme=\"b\" id=\"setDate\" />\n";
    	htmlOut += "</div>\n";
    	
    	htmlOut += "</fieldset>\n";
    	
    	// We need the hidden field for the Javascript redirect
    	htmlOut += "<input type=\"hidden\" id=\"curUrl\" name=\"curUrl\" value=\"" + genUrl(request) + "\" />\n";
    	
    	htmlOut += "</form>\n";
    	
    	htmlOut += "</div>\n";
		
    	// End content
    	htmlOut += "</div>\n";
    	
    	return htmlOut;
    }
    
    /**
     * Generates the header navigation toolbar for the current viewport
     */
    public String generateHeader(HttpServletRequest request, Calendar calendarview)
    {        
		// Start header area
    	String htmlOut = "<div data-role=\"header\" data-position=\"fixed\" data-tap-toggle=\"false\">\n";

    	// Display only the previous and next week buttons on the main view
    	if (request.getParameter("detail") == null) {
    		// Last week
    		htmlOut += "<a data-iconpos=\"notext\" data-icon=\"arrow-l\" class=\"ui-btn-left\" data-direction=\"reverse\" data-transition=\"slide\" href=\"" + genUrl(request) + date2Url(calendarview) + "&amp;prev=1\">" + getI18n().getString("btn_previous") + "</a>\n";
    	
    		// Next week
    		htmlOut += "<a data-iconpos=\"notext\" data-icon=\"arrow-r\" class=\"ui-btn-right\" data-transition=\"slide\" href=\"" + genUrl(request) + date2Url(calendarview) + "&amp;next=1\">" + getI18n().getString("btn_next") + "</a>\n";
    	}
    	
    	// Catch display view for setting the right title
    	String headerTitle;
    	if (request.getParameter("detail") != null) {
    		headerTitle = getI18n().getString("detail_view");
    	} else {
    		headerTitle = getI18n().getString("default_title");
    	}
    	
    	// set title
    	htmlOut += "<h1>" + headerTitle + "</h1>\n";
    	
    	// End header area
    	htmlOut += "</div>\n";
    	
    	return htmlOut;
    }
    
    /**
     * Generates the footer navigation toolbar for the current viewport 
     */
    public String generateFoooter(HttpServletRequest request)
    {
    	String htmlOut = "";
    	
    	// generate the current footer HTML-Code
    	htmlOut += "<div data-role=\"footer\" data-position=\"fixed\" data-fullscreen=\"true\">\n";
    	
    	// generate navbar for footer 
    	htmlOut += 	"<div data-role=\"navbar\">\n" +
    				"<ul>\n";
			
    	// display only the today and the set date buttons on the main view
    	if (request.getParameter("detail") == null) {
	    	// today active?
	    	String isActive = "";
	    	if (request.getParameter("today") != null) {
	    		isActive = " ui-btn-active ui-state-persist";
	    	}
	    	
	    	// buttons for setDate Calendar view
			htmlOut += "<li><a data-transition=\"pop\" class=\"" + isActive + "\" href=\"" + genUrl(request) + "today=1\">" + getI18n().getString("btn_today") + "</a></li>\n";
			htmlOut += "<li><a data-rel=\"dialog\" href=\"#pickdate\">" + getI18n().getString("btn_date") + "</a></li>\n";
    	} else {
    		// buttons for detail page
        	Calendar cal = Calendar.getInstance();
        	cal.setTime(model.getSelectedDate());
    		htmlOut += "<li><a href=\"" + genUrl(request, false) + date2Url(cal) + "\" data-direction=\"reverse\" >" + getI18n().getString("btn_back") + "</a></li>\n";
    		
    		// show only this link for the native android client
    		if (request.getParameter("android-client") != null) {
    			// only need the reservationId 
    			String[] splits = request.getParameter("detail").split("_");
    			
    			// only 2 results because the detailId-String looks like reservID_appointmID
    			if (splits.length == 2) {
    				htmlOut += "<li><a href=\"raplaclient://action/edit/reservation/" + splits[0]  + "\">" + getI18n().getString("btn_edit") + "</a></li>\n";
    			}
    		}
    	}

    	// end navbar
		htmlOut +=	"</ul>\n" +
					"</div>\n";
		
		// end footer
		htmlOut +=	"</div>\n";
		
		return htmlOut;
    }
    
    /**
     * Generates URL String by given request without the set date parameters
     * 
     * @param request
     * @static
     * @return URL String
     */
    public static String genUrl(HttpServletRequest request) {
    	return genUrl(request, false);
    }
    
    /**
     * Generates URL String with leading "&" by given request
     * 
     * With parseDate: rapla?page=mobile&user=admin&file=Default&day=15&month=12&year=2011&
     * Without parseDate: rapla?page=mobile&user=admin&file=Default&
     * 
     * @param request Current request
     * @param parseDate Add the date parameters to the URL if given
     * @static
     * @return URL String
     */
    public static String genUrl(HttpServletRequest request, boolean parseDate)
    {
    	// base
    	String url = "rapla?";
    	String[] params;
    	
    	// Configure parameters to be parsed later
    	if (parseDate == false) {
    		params = new String[] { "page", "user", "file", "android-client" };
    	} else {
    		params = new String[] { "page", "user", "file", "day", "month", "year", "android-client" };
    	}
   
    	// parse the parameters of the current page
    	for (int i=0; i < params.length; i++) {
    		// add only configured parameters
	    	if (request.getParameter(params[i]) != null) {
	    		url += params[i] + "=" + request.getParameter(params[i]) + "&amp;";
	    	}
    	}
    		
    	return url;
    }
    
    /**
     * Generate the set date part for the URL by given calendar
     * 
     * @param calendarview
     * @static
     * @return Date URL String, for instance day=01&month=02&yearh=2011
     */
    public static String date2Url(Calendar calendarview)
    {	
    	// String format to create leading zeros
    	String format = String.format("%%0%dd", 2);
    	
    	// get the values from the calendar
        int day = calendarview.get(Calendar.DATE);
        int month = calendarview.get(Calendar.MONTH) +1;
        int year = calendarview.get(Calendar.YEAR);
        
        // build the url string with the given date
        String dateUrl = "day=" + String.format(format, day) + "&amp;month=" + String.format(format, month) + "&amp;year=" + year;
        
        return dateUrl;
    }
    
    /**
     * Generate a date string by given calendar object
     * @param calendar
     * @static
     * @return date string, for instance: 2011-01-23 => YYYY-MM-DD
     */
    public static String calendarDateString(Calendar calendar)
    {
    	// String format to create leading zeros
    	String format = String.format("%%0%dd", 2);
    	
    	// get the values from the calendar
    	int day = calendar.get(Calendar.DAY_OF_MONTH);
        int month = calendar.get(Calendar.MONTH) +1;
        int year = calendar.get(Calendar.YEAR);
        
        // build the dateString
        String dateString = "" + year + "-" + String.format(format, month) + "-" +  String.format(format,day);
        
    	return dateString;
    }
    
    /**
     * Generate a dateWeekStamp by given calendar
     * 
     * @param calendarview
     * @static
     * @return dateStamp string, for instance: 10102011 (WeekMonthYear)
     */
    public static String dateWeekStamp(Calendar calendarview)
    {
    	// String format to create leading zeros
    	String format = String.format("%%0%dd", 2);
    	
    	// get the values from the calendar
    	int week = calendarview.get(Calendar.WEEK_OF_MONTH);
        int month = calendarview.get(Calendar.MONTH) +1;
        int year = calendarview.get(Calendar.YEAR);
        
        // build the dateWeekStamp
        String dateWeekStamp = "" + String.format(format, week) + String.format(format, month) + year;
        
        return dateWeekStamp;
    }
    
    /**
     * Prints the HTML error messages inside a div container 
     * The error messages can be styled by the css-class errorMsg
     * 
     * @param message This message will displayed inside the error div
     * @return String error message
     */
    public static String htmlError(String message)
    {
    	return "<div class=\"errorMsg\">" + message + "</div>\n";
    }
    

	@Override
    public int getIncrementSize() {
        return Calendar.WEEK_OF_YEAR;
    }
	
	/**
	 * Init Configuration in order to be able for querying the config
	 * 
	 * @return void
	 */
    private void initConfiguration() {
        try {
            ClientFacade facade = getClientFacade();
            Preferences preferences = (Preferences) facade.edit(facade.getPreferences(null));
            RaplaConfiguration raplaConfiguration = (RaplaConfiguration) preferences.getEntry("org.rapla.plugin");
            config = raplaConfiguration.find("class", MobilePlugin.PLUGIN_CLASS);
        } catch (RaplaException e) {
            getLogger().error("Cannot read plugin configuration");
        }
    }
}

