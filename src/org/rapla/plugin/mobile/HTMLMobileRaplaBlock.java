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
import org.rapla.framework.RaplaContext;
import org.rapla.framework.RaplaContextException;
import org.rapla.plugin.abstractcalendar.AbstractRaplaBlock;
import org.rapla.plugin.abstractcalendar.HTMLRaplaBlock;


class HTMLMobileRaplaBlock extends HTMLRaplaBlock implements HTMLBlock {

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
        
       
        return buf.toString();
    }
}
