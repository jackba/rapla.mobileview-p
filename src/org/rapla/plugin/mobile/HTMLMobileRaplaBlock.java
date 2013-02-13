/*--------------------------------------------------------------------------*
 | Copyright (C) 2006  Christopher Kohlhaas                                 |
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


import org.rapla.components.calendarview.html.HTMLBlock;
import org.rapla.components.util.xml.XMLWriter;
import org.rapla.entities.domain.Allocatable;
import org.rapla.entities.domain.internal.AppointmentImpl;
import org.rapla.entities.storage.RefEntity;
import org.rapla.plugin.abstractcalendar.AbstractRaplaBlock;


class HTMLMobileRaplaBlock extends AbstractRaplaBlock implements HTMLBlock {
	HTMLMobileRaplaBlock() {
		super();
	}

	private int m_day;
    private int m_row;
    private int m_rowCount;
    private int index = 0;

    private String curUrl = "";
    private boolean userColor = false;

    public void setCurUrl(String url) {
    	curUrl = url;
    }

    public void setUserColor(boolean value)
    {
    	userColor = value;
    }
    
    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public void setRowCount(int rows) {
        m_rowCount = rows;
    }

    public void setRow(int row) {
        m_row = row;
    }

    public int getRowCount() {
        return m_rowCount;
    }

    public int getRow() {
        return m_row;
    }

    public void setDay(int day) {
        m_day = day;
    }

    public int getDay() {
        return m_day;
    }

    /**
     * Returns only the Background color if user defined colors are enabled
     * 
     * @return String
     */
    public String getBackgroundColor() {
    	if (userColor) {
    		return getColorsAsHex()[0];
    	} else {
    		return "";
    	}
    }

    /**
     * Return to current block with the right information
     * 
     * @return String
     */
    public String toString() {
        StringBuffer buf = new StringBuffer();
        String label = XMLWriter.encode(getName(getReservation()));
        String timeString = getTimeString(false);

        // check if its anonymous
        if (getContext().isAnonymous()) {
            String anonymous = "&#160;&#160;&#160;&#160;???";
            if (timeString != null) {
                return timeString + " " + anonymous;
            } else {
                return anonymous;
            }
        }

        // is there a time string?
        if (timeString != null) {
            label = timeString + "<br/>" + label;
        }
        
        // get Appointment for ID
        AppointmentImpl curAppointment = ((AppointmentImpl) getAppointment());
        
        // Remove the org.rapla we don't need them for GET-Parameter
        String appointmendId = ((RefEntity) curAppointment).getId().toString().replace("org.rapla.entities.domain.Appointment", "");
        String reservationId = ((RefEntity)(curAppointment.getReservation())).getId().toString().replace("org.rapla.entities.domain.Reservation_", "");
        
        // build ID-String for detail view. Example string: detail=RESERVATIONID_APPOINTMENTID
        String blockDetailUrl = curUrl + "&amp;detail=" + reservationId + appointmendId;
        buf.append("<a href=\"" + blockDetailUrl + "\">"); 
                 
        buf.append(label);

        // show only visible persons
        if  (getBuildContext().isPersonVisible()) {
            Allocatable[] persons = getReservation().getPersons();
            for (int i=0; i<persons.length;i ++) {
                if (!getContext().isVisible(persons[i])) {
                    continue;
                }
                
                buf.append("<br />");
                buf.append("<span class=\"person\">");
                buf.append(XMLWriter.encode(getName(persons[i])));
                buf.append("</span>\n");
            }
        }
        
        // show only visible resources
        if  (getBuildContext().isResourceVisible()) {
            Allocatable[] resources = getReservation().getResources();
            for (int i=0; i<resources.length;i ++) {
                if (!getContext().isVisible(resources[i])) {
                    continue;
                }
                buf.append("<br />");
                buf.append("<span class=\"resource\">");
                buf.append(XMLWriter.encode(getName(resources[i])));
                buf.append("</span>");
            }
        }
        
        buf.append("</a>\n");
        return buf.toString();
    }
}
