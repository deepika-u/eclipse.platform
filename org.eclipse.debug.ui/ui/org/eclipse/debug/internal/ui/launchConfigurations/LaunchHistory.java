package org.eclipse.debug.internal.ui.launchConfigurations;

/**********************************************************************
Copyright (c) 2000, 2002 IBM Corp.  All rights reserved.
This file is made available under the terms of the Common Public License v1.0
which accompanies this distribution, and is available at
http://www.eclipse.org/legal/cpl-v10.html
**********************************************************************/

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationListener;
import org.eclipse.debug.core.ILaunchListener;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.debug.internal.ui.DebugUIPlugin;
import org.eclipse.debug.ui.IDebugUIConstants;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;

/**
 * A history of launches and favorites for a launch group
 */
public class LaunchHistory implements ILaunchListener, IPropertyChangeListener, ILaunchConfigurationListener {

	private LaunchGroupExtension fGroup;
	
	private List fHistory = new ArrayList();
	private List fFavorites = new ArrayList();
	private boolean fDirty = false;
	private ILaunchConfiguration fRecentLaunch;
	
	/**
	 * Creates a new launch history for the given launch group
	 */
	public LaunchHistory(LaunchGroupExtension group) {
		fGroup = group;
		ILaunchManager manager = DebugPlugin.getDefault().getLaunchManager(); 
		manager.addLaunchListener(this);
		manager.addLaunchConfigurationListener(this);
		DebugUIPlugin.getDefault().getPreferenceStore().addPropertyChangeListener(this);
	}
	
	/**
	 * Disposes this history
	 */
	public void dispose() {
		ILaunchManager manager = DebugPlugin.getDefault().getLaunchManager();
		manager.removeLaunchListener(this);
		manager.removeLaunchConfigurationListener(this);
		DebugUIPlugin.getDefault().getPreferenceStore().removePropertyChangeListener(this);
	}

	/**
	 * @see org.eclipse.debug.core.ILaunchListener#launchAdded(org.eclipse.debug.core.ILaunch)
	 */
	public void launchAdded(ILaunch launch) {
		if (accepts(launch)) {
			addHistory(launch.getLaunchConfiguration());
			setRecentLaunch(launch.getLaunchConfiguration());
		}
	}
	
	/**
	 * Adds the givev configuration to this hisotry
	 * 
	 * @param configuration	 */
	protected void addHistory(ILaunchConfiguration configuration) {
		clearDirty();
		if (fFavorites.contains(configuration)) {
			return;
		}
		// might be reconstructing history
		if (checkIfFavorite(configuration)) {
			return;
		}
		int index = fHistory.indexOf(configuration);
		if (index < 0) {
			fHistory.add(0, configuration);
			resizeHistory();
			setDirty();
		} else if (index > 0) {
			// move to first
			for (int i = index; i > 0; i--) {
				fHistory.set(i, fHistory.get(i -1));
			}
			fHistory.set(0, configuration);
			setDirty();
		}	
		save();
	}
	
	/**
	 * Saves if dirty
	 */
	private void save() {
		if (isDirty()) {
			try {
				LaunchConfigurationManager.getDefault().persistLaunchHistory();
			} catch (CoreException e) {
				DebugUIPlugin.log(e);
			} catch (IOException e) {
				DebugUIPlugin.log(e);
			}
		}
	}
	
	/**
	 * Clears the dirty flag
	 */
	private void clearDirty() {
		fDirty = false;
	}
	
	/**
	 * Sets the dirty flag
	 */
	private void setDirty() {
		fDirty = true;
	}
	
	/**
	 * Returns the dirty state
	 */
	private boolean isDirty() {
		return fDirty;
	}	

	/**
	 * @see org.eclipse.debug.core.ILaunchListener#launchChanged(org.eclipse.debug.core.ILaunch)
	 */
	public void launchChanged(ILaunch launch) {
	}

	/**
	 * @see org.eclipse.debug.core.ILaunchListener#launchRemoved(org.eclipse.debug.core.ILaunch)
	 */
	public void launchRemoved(ILaunch launch) {
	}

	/**
	 * Returns the most recently launched configuration in this history, or
	 * <code>null</code> if none.
	 * 
	 * @return the most recently launched configuration in this history, or
	 * <code>null</code> if none 	 */
	public ILaunchConfiguration getRecentLaunch() {
		return fRecentLaunch;
	}
	
	/**
	 * Sets the most recently launched configuration in this history, or
	 * <code>null</code> if none.
	 */
	protected void setRecentLaunch(ILaunchConfiguration configuration) {
		if (accepts(configuration)) {
			if (!configuration.equals(fRecentLaunch)) {
				fRecentLaunch = configuration;
				setDirty();
				save();
			}
		}
	}	
	
	/**
	 * Returns the launch configuration in this history, in most recently
	 * launched order.
	 * 
	 * @return launch history	 */
	public ILaunchConfiguration[] getHistory() {
		return (ILaunchConfiguration[])fHistory.toArray(new ILaunchConfiguration[fHistory.size()]);
	}
	
	/**
	 * Returns the favorite launch configurations in this history, in the order
	 * they were created.
	 * 
	 * @return launch favorites	 */
	public ILaunchConfiguration[] getFavorites() {
		return (ILaunchConfiguration[])fFavorites.toArray(new ILaunchConfiguration[fFavorites.size()]);
	}
	
	/**
	 * Sets this container's launch history.
	 * 
	 * @param history	 */
	public void setHistory(ILaunchConfiguration[] history) {
		fHistory = new ArrayList(history.length);
		for (int i = 0; i < history.length; i++) {
			fHistory.add(history[i]);
		}
		resizeHistory();
		setDirty();
		save();
	}
	
	/**
	 * Sets this container's favorites.
	 * 
	 * @param favorites
	 */
	public void setFavorites(ILaunchConfiguration[] favorites) {
		fFavorites = new ArrayList(favorites.length);
		for (int i = 0; i < favorites.length; i++) {
			fFavorites.add(favorites[i]);
		}
		setDirty();
		save();
	}	
	
	/**
	 * Adds the given configuration to the favorites list.
	 * 
	 * @param configuration	 */
	public void addFavorite(ILaunchConfiguration configuration) {
		clearDirty();
		if (!fFavorites.contains(configuration)) {
			fFavorites.add(configuration);
			fHistory.remove(configuration);
			setDirty();
		}
		save();
	}
	
	/**
	 * Returns the launch group associated with this history
	 * 
	 * @return group	 */
	public LaunchGroupExtension getLaunchGroup() {
		return fGroup;
	}
	
	/**
	 * Returns whether the given launch is included in the group assocaited with
	 * this launch history.
	 * 
	 * @param launch	 * @return boolean	 */
	protected boolean accepts(ILaunch launch) {
		ILaunchConfiguration configuration = launch.getLaunchConfiguration();
		if (configuration == null) {
			return false;
		}
		return accepts(configuration);
	}
	
	/**
	 * Returns whether the given configruation is included in the group
	 * associated with this launch history.
	 * 
	 * @param launch
	 * @return boolean
	 */
	public boolean accepts(ILaunchConfiguration configuration) {
		try {
			if (configuration.getType().supportsMode(getLaunchGroup().getMode())) {
				String launchCategory = null;
				launchCategory = configuration.getCategory();
				String category = getLaunchGroup().getCategory();
				if (launchCategory == null || category == null) {
					return launchCategory == category;
				}
				return category.equals(launchCategory) && LaunchConfigurationManager.isVisible(configuration);
			}
		} catch (CoreException e) {
			DebugUIPlugin.log(e);
		}
		return false;
	}	
	
	/**
	 * @see org.eclipse.jface.util.IPropertyChangeListener#propertyChange(org.eclipse.jface.util.PropertyChangeEvent)
	 */
	public void propertyChange(PropertyChangeEvent event) {
		if (event.getProperty().equals(IDebugUIConstants.PREF_MAX_HISTORY_SIZE)) {
			resizeHistory();
			save();
		}
	}
	
	/**
	 * The max history size has changed - remove any histories if current
	 * collection is too long.
	 */
	protected void resizeHistory() {
		int max = getMaxHistorySize();
		while (fHistory.size() > max) {
			fHistory.remove(fHistory.size() - 1);
			setDirty();
		}
	}

	/**
	 * Returns the maximum number of entries allowed in this history
	 * 
	 * @return the maximum number of entries allowed in this history	 */
	protected int getMaxHistorySize() {
		return DebugUIPlugin.getDefault().getPreferenceStore().getInt(IDebugUIConstants.PREF_MAX_HISTORY_SIZE);
	}
	
	/**
	 * @see org.eclipse.debug.core.ILaunchConfigurationListener#launchConfigurationAdded(org.eclipse.debug.core.ILaunchConfiguration)
	 */
	public void launchConfigurationAdded(ILaunchConfiguration configuration) {
		checkIfFavorite(configuration);
	}
	
	/**
	 * Adds the given config to the favorites list if it is a favorite, and
	 * returns whether the config was added to the favorites list.
	 * 
	 * @param configuration
	 * @return whether added to the favorites list
	 */
	protected boolean checkIfFavorite(ILaunchConfiguration configuration) {
		// update favorites
		if (configuration.isWorkingCopy()) {
			return false;
		}
		try {
			List favoriteGroups = configuration.getAttribute(IDebugUIConstants.ATTR_FAVORITE_GROUPS, (List)null);
			if (favoriteGroups == null) {
				// check deprecated attributes for backwards compatibility
				String groupId = getLaunchGroup().getIdentifier();
				boolean fav = false;
				if (groupId.equals(IDebugUIConstants.ID_DEBUG_LAUNCH_GROUP)) {
					fav = configuration.getAttribute(IDebugUIConstants.ATTR_DEBUG_FAVORITE, false);
				} else if (groupId.equals(IDebugUIConstants.ID_RUN_LAUNCH_GROUP)) {
					fav = configuration.getAttribute(IDebugUIConstants.ATTR_RUN_FAVORITE, false);
				}
				if (fav) {
					addFavorite(configuration);
					return true;
				} else {
					removeFavorite(configuration);
					return false;
				}
			} else if (favoriteGroups.contains(getLaunchGroup().getIdentifier())) {
				addFavorite(configuration);
				return true;
			} else {
				removeFavorite(configuration);
				return false;
			}
		} catch (CoreException e) {
		}		
		return false;
	}
	
	/**
	 * Revmoves the given config from the favorites list, if needed.
	 * 
	 * @param configuration
	 */
	protected void removeFavorite(ILaunchConfiguration configuration) {
		if (fFavorites.contains(configuration)) {
			fFavorites.remove(configuration);
			setDirty();
			save();
		}
	}

	/**
	 * @see org.eclipse.debug.core.ILaunchConfigurationListener#launchConfigurationChanged(org.eclipse.debug.core.ILaunchConfiguration)
	 */
	public void launchConfigurationChanged(ILaunchConfiguration configuration) {
		checkIfFavorite(configuration);
	}

	/**
	 * @see org.eclipse.debug.core.ILaunchConfigurationListener#launchConfigurationRemoved(org.eclipse.debug.core.ILaunchConfiguration)
	 */
	public void launchConfigurationRemoved(ILaunchConfiguration configuration) {
		boolean removed = fHistory.remove(configuration);
		removed = fFavorites.remove(configuration) || removed;
		if (removed) {
			setDirty();
			save();
			if (configuration.equals(fRecentLaunch)) {
				if (fHistory.isEmpty()) {
					if (!fFavorites.isEmpty()) {
						fRecentLaunch = (ILaunchConfiguration)fFavorites.get(0);
					}
				} else {
					fRecentLaunch = (ILaunchConfiguration)fHistory.get(0);
				}
			}
		}
	}

}
