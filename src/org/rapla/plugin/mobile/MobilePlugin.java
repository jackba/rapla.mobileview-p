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

import org.rapla.client.ClientServiceContainer;
import org.rapla.client.RaplaClientExtensionPoints;
import org.rapla.components.xmlbundle.I18nBundle;
import org.rapla.components.xmlbundle.impl.I18nBundleImpl;
import org.rapla.framework.Configuration;
import org.rapla.framework.Container;
import org.rapla.framework.PluginDescriptor;
import org.rapla.framework.RaplaContextException;
import org.rapla.framework.TypedComponentRole;
import org.rapla.framework.logger.Logger;

public class MobilePlugin  implements PluginDescriptor<ClientServiceContainer>
{
	// Resource file with language definitions
    public static final TypedComponentRole<I18nBundle> RESOURCE_FILE = new TypedComponentRole<I18nBundle>( MobilePlugin.class.getPackage().getName() + ".MobileResources");
    
    // plugin entry definition
    public static final String PLUGIN_ENTRY = "org.rapla.plugin.mobile";
    
    // set selected
    public static final String HTML_VIEW = PLUGIN_ENTRY + ".selected";
    
    // set plugin class
    public static final String PLUGIN_CLASS = MobilePlugin.class.getName();
    
    // use user colors for appointment blocks?
    public static final String ENABLE_USER_COLOR = "enable_user_color";
   
    // plugin enabled
    public static boolean ENABLE_BY_DEFAULT = true;
    
    
    public void provideServices(ClientServiceContainer container, Configuration config) throws RaplaContextException {
    	// register select box for enabling user defined colors for appointments
    	container.addContainerProvidedComponent(RaplaClientExtensionPoints.PLUGIN_OPTION_PANEL_EXTENSION, MobilePluginOption.class);
    	
    	// add resource file in order to get later the language definitions
    	container.addContainerProvidedComponent(RESOURCE_FILE, I18nBundleImpl.class,I18nBundleImpl.createConfig(RESOURCE_FILE.getId()));
        
    	
    }

 

}

