package org.rapla.plugin.mobile.server;

import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.Locale;

import org.rapla.entities.Timestamp;
import org.rapla.entities.domain.Appointment;
import org.rapla.entities.domain.Reservation;
import org.rapla.entities.dynamictype.Classifiable;
import org.rapla.entities.dynamictype.Classification;
import org.rapla.framework.RaplaContext;
import org.rapla.gui.internal.view.ReservationInfoUI;

public class AppointmentInfoMobile extends ReservationInfoUI {
    public AppointmentInfoMobile(RaplaContext sm)  {
        super(sm);
    }

    /**
     * Generates the full information for the detail page
     */
    public String getTooltip(Object object) {
        Appointment appointment = (Appointment) object;
        Reservation reservation =  appointment.getReservation();
        
        // start generating the html output code
        StringBuffer buf = new StringBuffer();
        
        buf.append("<div class=\"smallInfo\">");
        // Modification and creation info
        insertModificationRow(reservation, buf);
        
        // Appointment summary
        insertAppointmentSummary(appointment, buf);
        
        // Classification of the reservation
        insertClassificationTitle(reservation, buf);
        
        // Create the ordered list with all attributes
        createOrderedList(getAttributes(reservation, null, null, true), buf, false);
        buf.append("</div>\n");
        
        return buf.toString();
    }
    
    /**
     * Create the appointment summary output
     */
    public void insertAppointmentSummary(Appointment appointment, StringBuffer buf) {
        buf.append("<h4>");
        buf.append(getAppointmentFormater().getSummary(appointment));
        buf.append("</h4>\n");
    }
    
    /**
     * Create a ordered list of the given attributes
     */
    public static void createOrderedList(Collection<Row> attributes,StringBuffer buf, boolean encodeValues)
    {
    	buf.append("<ul data-role=\"listview\">");
    	
    	Iterator<Row> it = attributes.iterator();
        while (it.hasNext()) {
        	Row att =  it.next();
            buf.append("<li>\n");
            String field = att.getField();
			encode(field,buf);
            if  ( field.length() > 0)
            {
            	buf.append(":");
            }
            buf.append("\n");
            buf.append("<p class=\"ui-li-aside\"><strong>");
            if (att.getValue() != null) {
            	// encode values if requested for better HTML support
                if (encodeValues) {
                    encode(att.getValue(),buf);
                } else {
                    buf.append(att.getValue());
                }
            }
            buf.append("</strong></p>\n");
            buf.append("</li>\n");
        }
    
    	buf.append("</ul>\n");
    }
    
    /**
     * Generates the created_at and last_changed information row
     */
    @Override
    protected void insertModificationRow( Timestamp timestamp, StringBuffer buf ) {
        final Date createTime = timestamp.getCreateTime();
        final Date lastChangeTime = timestamp.getLastChangeTime();
        if (lastChangeTime != null) {
        	buf.append("<br/>");
        	buf.append("<p class=\"ui-li-desc\"><strong>");
        	
            if (createTime != null) {
                buf.append(getString("created_at"));
                buf.append(" ");
                buf.append(getRaplaLocale().formatDate(createTime));
                buf.append(", ");
            }
            
            buf.append(getString("last_changed"));
            buf.append(" ");
            buf.append(getRaplaLocale().formatDate(lastChangeTime));
            buf.append("</strong></p>");
            buf.append("\n");
        }
    }
    
    /**
     * Insert the classificationTitle
     */
    public void insertClassificationTitle( Classifiable classifiable, StringBuffer buf ) {
        Classification classification = classifiable.getClassification();
        
        buf.append( "<h2>");
        Locale locale = getRaplaLocale().getLocale();
        encode(classification.getType().getName(locale), buf);
        buf.append( "</h2>\n");
    }
}
