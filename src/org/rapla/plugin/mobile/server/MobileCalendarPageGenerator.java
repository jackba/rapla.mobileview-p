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
package org.rapla.plugin.mobile.server;

import org.rapla.framework.RaplaContext;
import org.rapla.framework.RaplaContextException;
import org.rapla.plugin.abstractcalendar.server.HTMLViewFactory;
import org.rapla.plugin.autoexport.server.CalendarPageGenerator;

public class MobileCalendarPageGenerator extends CalendarPageGenerator 
{
	public MobileCalendarPageGenerator(RaplaContext context) throws  RaplaContextException
    {
        super(context);
    }

	@Override
	protected HTMLViewFactory getFactory(String viewId) {
		return super.getFactory("mobile_"+viewId);
	}

}
