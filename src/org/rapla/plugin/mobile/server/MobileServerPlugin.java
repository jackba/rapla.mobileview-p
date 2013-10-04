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

import org.rapla.components.xmlbundle.impl.I18nBundleImpl;
import org.rapla.framework.Configuration;
import org.rapla.framework.PluginDescriptor;
import org.rapla.framework.RaplaContextException;
import org.rapla.plugin.mobile.MobilePlugin;
import org.rapla.server.RaplaServerExtensionPoints;
import org.rapla.server.ServerServiceContainer;

public class MobileServerPlugin  implements PluginDescriptor<ServerServiceContainer>
{
    
    public void provideServices(ServerServiceContainer container, Configuration config) throws RaplaContextException {
    	// add resource file in order to get later the language definitions
    	container.addContainerProvidedComponent(MobilePlugin.RESOURCE_FILE, I18nBundleImpl.class,I18nBundleImpl.createConfig(MobilePlugin.RESOURCE_FILE.getId()));
        
    	// check if the plugin is enabled
    	if (!config.getAttributeAsBoolean("enabled", MobilePlugin.ENABLE_BY_DEFAULT)) {
        	return;
        }
        
    	// Mobile WEEK_VIEW
    	container.addContainerProvidedComponent (RaplaServerExtensionPoints.HTML_CALENDAR_VIEW_EXTENSION,MobileWeekViewFactory.class);
	        
        container.addWebpage( "mobile",MobileCalendarPageGenerator.class);
    }

 

}

