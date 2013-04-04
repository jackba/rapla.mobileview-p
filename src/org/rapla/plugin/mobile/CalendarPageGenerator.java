/*--------------------------------------------------------------------------*
 | Copyright (C) 2011 Robert Hoppe - http://www.katado.com                  |
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

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.rapla.components.util.IOUtil;
import org.rapla.entities.User;
import org.rapla.facade.CalendarSelectionModel;
import org.rapla.facade.RaplaComponent;
import org.rapla.framework.RaplaContext;
import org.rapla.framework.RaplaException;
import org.rapla.gui.ViewFactory;
import org.rapla.plugin.RaplaExtensionPoints;
import org.rapla.plugin.autoexport.AutoExportPlugin;
import org.rapla.servletpages.RaplaPageGenerator;

public class CalendarPageGenerator extends RaplaComponent implements RaplaPageGenerator
{
    public CalendarPageGenerator(RaplaContext context) throws RaplaException
    {
        super(context);
    }

    public void generatePage(ServletContext servletContext, HttpServletRequest request, HttpServletResponse response)
            throws IOException, ServletException
    {
        try
        {
            // get by url given parameters user and filename
        	String username = request.getParameter("user");
            String filename = request.getParameter("file");
            
            // Load Calendar by user and filename
            CalendarSelectionModel model = null;
            User user = getQuery().getUser(username);
            model = getModification().newCalendarModel(user);
            model.load(filename);
            
            // check if calendar is allowed for VIEW
            final Object isSet = model.getOption(AutoExportPlugin.HTML_EXPORT);       
            if (isSet == null || isSet.equals("false")) {
            	throw new RaplaException( "Exportfile with name '" + filename + "' not published!" );
            }
            
            // force for getting mobile version of current view
            final String viewId = "mobile_" + model.getViewId();
            ViewFactory factory = (ViewFactory) getService( RaplaExtensionPoints.CALENDAR_VIEW_EXTENSION
                    + "/"
                    + viewId);

            if (factory != null) {
            	// okay there is a view lets display it mobile optimized
                RaplaPageGenerator currentView = factory.createHTMLView(getContext(), model);
                if (currentView != null ) {
                    currentView.generatePage(servletContext, request, response);
                } else {
                	// createHTMLView not available
                    writeError(response, "No mobile view available for '"
                            + filename
                            + "'. Rapla has currently no mobile html support for the view with the id '"
                            + viewId
                            + "'.");
                }
            } else {
            	// factor not available for requested calendar
            	writeError(response, "No mobile view available for '"
                        + filename
                        + "'. Please install and select the plugin for "
                        + viewId);
            }
        } catch (Exception ex) {
        	writeError(response, IOUtil.getStackTraceAsString(ex));
            throw new ServletException(ex);
        }
    }

    /**
     * Print given error messages to user
     */
    private void writeError(HttpServletResponse response, String message) throws IOException
    {
        response.setContentType("text/html; charset=" + getRaplaLocale().getCharsetNonUtf());
        java.io.PrintWriter out = response.getWriter();
        out.println(message);
    }

}
