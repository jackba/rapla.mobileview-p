/*--------------------------------------------------------------------------*
 | Copyright (C) 2006 Christopher Kohlhaas                                  |
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

import java.awt.BorderLayout;
import java.util.Locale;

import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.rapla.components.layout.TableLayout;
import org.rapla.framework.Configuration;
import org.rapla.framework.DefaultConfiguration;
import org.rapla.framework.PluginDescriptor;
import org.rapla.framework.RaplaContext;
import org.rapla.framework.RaplaException;
import org.rapla.gui.DefaultPluginOption;
import org.rapla.plugin.autoexport.AutoExportPlugin;

public class MobilePluginOption extends DefaultPluginOption {
   
	// init checkbox
    JCheckBox useUserColor = new JCheckBox();
    
    public MobilePluginOption(RaplaContext sm) {
        super(sm);
    }

    // build new jPanel for the checkbox
    protected JPanel createPanel() throws RaplaException {
        JPanel panel = super.createPanel();
        JPanel content = new JPanel();
        double[][] sizes = new double[][] {
            {5,TableLayout.PREFERRED, 5,TableLayout.FILL,5}
            ,{TableLayout.PREFERRED,5,TableLayout.PREFERRED, 5, TableLayout.PREFERRED, 5, TableLayout.PREFERRED, 5, TableLayout.PREFERRED, 5, TableLayout.PREFERRED, 5, TableLayout.PREFERRED}
        };
        TableLayout tableLayout = new TableLayout(sizes);
        content.setLayout(tableLayout);
        
        // notice user if html export is not enabled
        if(!getContext().has(AutoExportPlugin.AUTOEXPORT_PLUGIN_ENABLED)) {
        	content.add(new JLabel("This plugin depends on the HTML Export Plugin. Please enable this plugin before."), "1,0,4,0");
        }
        
        content.add(new JLabel("Use user defined colors for appointment blocks"), "1,4");
        content.add(useUserColor,"3,4");
        panel.add( content, BorderLayout.CENTER);
        return panel;
    }
    

    protected void addChildren(DefaultConfiguration newConfig) {
        newConfig.setAttribute(MobilePlugin.ENABLE_USER_COLOR, useUserColor.isSelected());
    }

    protected void readConfig(Configuration config)   {
    	useUserColor.setSelected( config.getAttributeAsBoolean(MobilePlugin.ENABLE_USER_COLOR, false));
    }

    public void show() throws RaplaException  {
        super.show();
    }
  
    public void commit() throws RaplaException {
        super.commit();
    }
    
    public Class<? extends PluginDescriptor> getPluginClass() {
        return MobilePlugin.class;
    }
    
    public String getName(Locale locale) {
        return "Mobile Plugin";
    }

}
