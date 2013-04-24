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
import org.rapla.plugin.RaplaClientExtensionPoints;
import org.rapla.plugin.RaplaServerExtensionPoints;
import org.rapla.plugin.mobile.MobilePlugin;
import org.rapla.server.ServerServiceContainer;
import org.rapla.servletpages.RaplaResourcePageGenerator;

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
        RaplaResourcePageGenerator resourcePageGenerator = container.getContext().lookup(RaplaResourcePageGenerator.class);
        
        // custom css definitions for the mobile view
        resourcePageGenerator.registerResource("mobile.css", "text/css", this.getClass().getResource("/org/rapla/plugin/mobile/server/css/mobile.css")); 
        
        // css definitions of the jQuery Mobile Framework
        resourcePageGenerator.registerResource("jquery.mobile.css", "text/css", this.getClass().getResource("/org/rapla/plugin/mobile/server/css/jquery.mobile.css"));
        
        // jQuery javascript library, the base for jQuery mobile
        resourcePageGenerator.registerResource("jquery.min.js", "text/javascript", this.getClass().getResource("/org/rapla/plugin/mobile/server/js/jquery.min.js")); 
        
        // Global preferences for jQuery Mobile
        resourcePageGenerator.registerResource("jquery.mobile.pref.js", "text/javascript", this.getClass().getResource("/org/rapla/plugin/mobile/server/js/jquery.mobile.pref.js"));
        
        // jQuery Mobile Framework
        resourcePageGenerator.registerResource("jquery.mobile.js", "text/javascript", this.getClass().getResource("/org/rapla/plugin/mobile/server/js/jquery.mobile.js"));
        
        // jQuery Mobile Plugin scroll view to realiese the horizontal scroll
        resourcePageGenerator.registerResource("jquery.mobile.scrollview.js", "text/javascript", this.getClass().getResource("/org/rapla/plugin/mobile/server/js/jquery.mobile.scrollview.js"));
        
        // jQuery easing effects
        resourcePageGenerator.registerResource("jquery.easing.js", "text/javascript", this.getClass().getResource("/org/rapla/plugin/mobile/server/js/jquery.easing.js"));
        
        // jQuery mobile Plugin datebox date picker 
        resourcePageGenerator.registerResource("jquery.mobile.datebox.min.js", "text/javascript", this.getClass().getResource("/org/rapla/plugin/mobile/server/js/jquery.mobile.datebox.min.js"));
        
        // custom Javascript functions to handle the functionality
        resourcePageGenerator.registerResource("script.js", "text/javascript", this.getClass().getResource("/org/rapla/plugin/mobile/server/js/script.js"));
        	        
        // register needed images for the jQuery mobile Framework
        resourcePageGenerator.registerResource("ajax-loader.png", "text/plain", this.getClass().getResource("/org/rapla/plugin/mobile/server/images/ajax-loader.png"));
        resourcePageGenerator.registerResource("ajax-loader.gif", "text/plain", this.getClass().getResource("/org/rapla/plugin/mobile/server/images/ajax-loader.gif"));
        resourcePageGenerator.registerResource("icons-18-black.png", "text/plain", this.getClass().getResource("/org/rapla/plugin/mobile/server/images/icons-18-black.png"));
        resourcePageGenerator.registerResource("icons-36-black.png", "text/plain", this.getClass().getResource("/org/rapla/plugin/mobile/server/images/icons-36-black.png"));
        resourcePageGenerator.registerResource("icons-18-white.png", "text/plain", this.getClass().getResource("/org/rapla/plugin/mobile/server/images/icons-18-white.png"));
        resourcePageGenerator.registerResource("icons-36-white.png", "text/plain", this.getClass().getResource("/org/rapla/plugin/mobile/images/server/icons-36-white.png"));
	        
        container.addWebpage( "mobile",MobileCalendarPageGenerator.class);
    }

 

}

