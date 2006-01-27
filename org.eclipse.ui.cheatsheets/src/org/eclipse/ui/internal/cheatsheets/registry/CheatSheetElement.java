/*******************************************************************************
 * Copyright (c) 2002, 2004 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.ui.internal.cheatsheets.registry;

import org.eclipse.core.runtime.*;
import org.eclipse.osgi.util.NLS;
import org.eclipse.ui.IPluginContribution;
import org.eclipse.ui.cheatsheets.CheatSheetListener;
import org.eclipse.ui.internal.cheatsheets.*;
import org.eclipse.ui.model.WorkbenchAdapter;
import org.eclipse.ui.model.IWorkbenchAdapter;
import org.osgi.framework.Bundle;

/**
 *	Instances represent registered cheatsheets or cheatsheets opened using the content file.
 */
public class CheatSheetElement extends WorkbenchAdapter implements IAdaptable, IPluginContribution {
	private String contentFile;
	private String id;
	private String name;
	private String description;
	private IConfigurationElement configurationElement;
	private String listenerClass;
	private boolean composite;
	private boolean registered = false;

	/**
	 *	Create a new instance of this class
	 *
	 *	@param name java.lang.String
	 */
	public CheatSheetElement(String name) {
		this.name = name;
	}

	/**
	 * Returns an object which is an instance of the given class
	 * associated with this object. Returns <code>null</code> if
	 * no such object can be found.
	 */
	public Object getAdapter(Class adapter) {
		if (adapter == IWorkbenchAdapter.class) {
			return this;
		}
		return Platform.getAdapterManager().getAdapter(this, adapter);
	}

	/**
	 * 
	 * @return IConfigurationElement
	 */
	public IConfigurationElement getConfigurationElement() {
		return configurationElement;
	}

	/**
	 *	Answer the contentFile parameter of this element
	 *
	 *	@return java.lang.String
	 */
	public String getContentFile() {
		return contentFile;
	}

	/**
	 *	Answer the description parameter of this element
	 *
	 *	@return java.lang.String
	 */
	public String getDescription() {
		return description;
	}

	/**
	 *	Answer the id as specified in the extension.
	 *
	 *	@return java.lang.String
	 */
	public String getID() {
		return id;
	}

	/**
	 * Returns the name of this cheatsheet element.
	 */
	public String getLabel(Object element) {
		return name;
	}

	/**
	 * Returns the listener class name of this cheatsheet element.
	 */
	public String getListenerClass() {
		return listenerClass;
	}

	/**
	 * 
	 * @param newConfigurationElement IConfigurationElement
	 */
	public void setConfigurationElement(IConfigurationElement newConfigurationElement) {
		configurationElement = newConfigurationElement;
	}

	/**
	 *	Set the contentFile parameter of this element
	 *
	 *	@param value the URL of the content file
	 */
	public void setContentFile(String value) {
		contentFile = value;
	}

	/**
	 *	Set the description parameter of this element
	 *
	 *	@param value java.lang.String
	 */
	public void setDescription(String value) {
		description = value;
	}

	/**
	 *	Set the id parameter of this element
	 *
	 *	@param value java.lang.String
	 */
	public void setID(String value) {
		id = value;
	}

	/**
	 * Set the listener class name of this element.
	 */
	public void setListenerClass(String value) {
		listenerClass = value;
	}

	public CheatSheetListener createListenerInstance() {
		if(listenerClass == null || configurationElement == null) {
			return null;
		}

		Class extClass = null;
		CheatSheetListener listener = null;
		String pluginId = configurationElement.getNamespace();

		try {
			Bundle bundle = Platform.getBundle(pluginId);
			extClass = bundle.loadClass(listenerClass);
		} catch (Exception e) {
			String message = NLS.bind(Messages.ERROR_LOADING_CLASS, (new Object[] {listenerClass}));
			IStatus status = new Status(IStatus.ERROR, ICheatSheetResource.CHEAT_SHEET_PLUGIN_ID, IStatus.OK, message, e);
			CheatSheetPlugin.getPlugin().getLog().log(status);
		}
		try {
			if (extClass != null) {
				listener = (CheatSheetListener) extClass.newInstance();
			}
		} catch (Exception e) {
			String message = NLS.bind(Messages.ERROR_CREATING_CLASS, (new Object[] {listenerClass}));
			IStatus status = new Status(IStatus.ERROR, ICheatSheetResource.CHEAT_SHEET_PLUGIN_ID, IStatus.OK, message, e);
			CheatSheetPlugin.getPlugin().getLog().log(status);
		}
		
		if (listener != null){
			return listener;
		}

		return null;
	}

	public String getLocalId() {
		return id;
	}

	public String getPluginId() {
		return configurationElement.getNamespace();
	}
	
	public void setComposite(boolean composite) {
		this.composite = composite;
	}

	public boolean isComposite() {
		return composite;
	}

	/**
	 * Get a URL which is saved with the state so the cheatsheet can later be 
	 * reopened from the state file.
	 * @return null if the cheatsheet was opened from the registry otherwise
	 * the URL of the content file.
	 */
	public String getRestorePath() {
		if (registered) {
			return null;
		} else {
		    return contentFile;
		}
	}

	public void setRegistered(boolean registered) {
		this.registered = registered;
	}

	public boolean isRegistered() {
		return registered;
	}

}
