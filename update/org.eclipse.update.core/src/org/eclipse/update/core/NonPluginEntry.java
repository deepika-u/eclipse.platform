/*******************************************************************************
 *  Copyright (c) 2000, 2009 IBM Corporation and others.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 * 
 *  Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.update.core;

import org.eclipse.update.core.model.*;

/**
 * Convenience implementation of non-plug-in entry.
 * <p>
 * This class may be instantiated or subclassed by clients.
 * </p> 
 * <p>
 * <b>Note:</b> This class/interface is part of an interim API that is still under development and expected to
 * change significantly before reaching stability. It is being made available at this early stage to solicit feedback
 * from pioneering adopters on the understanding that any code that uses this API will almost certainly be broken
 * (repeatedly) as the API evolves.
 * </p>
 * @see org.eclipse.update.core.INonPluginEntry
 * @see org.eclipse.update.core.model.NonPluginEntryModel
 * @since 2.0
 * @deprecated The org.eclipse.update component has been replaced by Equinox p2. This
 * provisional API was never promoted to stable API, and may be removed from a future release of the platform.
 */
public class NonPluginEntry
	extends NonPluginEntryModel
	implements INonPluginEntry {

	/**
	 * Non-plug-in entry default constructor
	 */
	public NonPluginEntry() {
		super();
	}
}