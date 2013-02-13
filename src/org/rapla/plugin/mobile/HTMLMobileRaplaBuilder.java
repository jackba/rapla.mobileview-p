package org.rapla.plugin.mobile;

import java.util.Calendar;
import java.util.Date;

import org.rapla.components.calendarview.Block;
import org.rapla.entities.configuration.Preferences;
import org.rapla.entities.configuration.RaplaConfiguration;
import org.rapla.facade.ClientFacade;
import org.rapla.framework.RaplaContext;
import org.rapla.framework.RaplaException;
import org.rapla.gui.CalendarOptionsImpl;
import org.rapla.plugin.abstractcalendar.RaplaBuilder;
import org.rapla.plugin.mail.MailPlugin;

class HTMLMobileRaplaBuilder extends RaplaBuilder {
    static String COLOR_NO_RESOURCE = "#BBEEBB";
    int m_rowsPerHour = 4;
    /** shared calendar instance. Only used for temporary stored values. */
    String m_html;
    int index = 0;
    
    // Helper variable for the currentUrl
    String curUrl;
    
    // Helper variable for userColor
    boolean userColor = false;
    
    public HTMLMobileRaplaBuilder(RaplaContext sm) throws RaplaException {
        super(sm);
    }
    
    /**
     * This URL is needed by the blocks for the detail view link
     * 
     * @param String urlString url of currently displayed page
     */
    public void setCurUrl(String urlString) {
    	this.curUrl = urlString;
    }
    
    /**
     * Enable user defined colors
     * 
     * @param boolean value Set true for userColor
     */
    public void setUserColor(boolean value)
    {
    	this.userColor = value;
    }
    
    /**
     * Create the Block
     */
    public Block createBlock(RaplaBlockContext blockContext, Date start, Date end) {
        HTMLMobileRaplaBlock block = new HTMLMobileRaplaBlock();
        block.setCurUrl(curUrl);
        block.setUserColor(userColor);
        block.setIndex(index++);
        block.setStart(start);
        block.setEnd(end);
        block.contextualize(blockContext);
               
        Calendar calendar = getRaplaLocale().createCalendar();
        calendar.setTime(start);
        int row = (int) (
            calendar.get(Calendar.HOUR_OF_DAY)* m_rowsPerHour
            + Math.round((calendar.get(Calendar.MINUTE) * m_rowsPerHour)/60.0)
            );
        block.setRow(row);
        block.setDay(calendar.get(Calendar.DAY_OF_WEEK));

        calendar.setTime(block.getEnd());
        int endRow = (int) (
            calendar.get(Calendar.HOUR_OF_DAY)* m_rowsPerHour
            + Math.round((calendar.get(Calendar.MINUTE) * m_rowsPerHour)/60.0)
            );
        int rowCount = endRow -row;
        block.setRowCount(rowCount);

        return block;
    }

}