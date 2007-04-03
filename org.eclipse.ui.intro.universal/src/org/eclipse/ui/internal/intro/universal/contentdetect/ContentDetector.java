/*******************************************************************************
 * Copyright (c) 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/

package org.eclipse.ui.internal.intro.universal.contentdetect;

import java.util.HashSet;
import java.util.Set;

import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.Platform;
import org.eclipse.ui.intro.IntroContentDetector;

public class ContentDetector extends IntroContentDetector {

	private static Set newContributors;
	private static boolean detectorCalled = false;
	
	public ContentDetector() {
	}

	public boolean isNewContentAvailable() {		
		try {
			detectorCalled = true;
			// If we have previously found new content no need to recompute
			if (newContributors != null && !newContributors.isEmpty()) {
				return true;
			}
			IExtension[] extensions = Platform
					.getExtensionRegistry()
					.getExtensionPoint("org.eclipse.ui.intro.configExtension").getExtensions(); //$NON-NLS-1$
			int numIntroExtensions = extensions.length;
      
			ContentDetectHelper helper = new ContentDetectHelper();
			int previous = helper.getExtensionCount();
			if (numIntroExtensions != previous) {
				helper.saveExtensionCount(numIntroExtensions);
				Set contributors = new HashSet();
				for (int i = 0; i < extensions.length; i++) {
					contributors.add(extensions[i].getContributor().getName());
				}
				if (numIntroExtensions > previous && previous != ContentDetectHelper.NO_STATE) {
					Set previousContributors = helper.getContributors();
					newContributors = helper.findNewContributors(contributors, previousContributors);
					helper.saveContributors(contributors);
					return true;
				}
				helper.saveContributors(contributors);
			}
		} catch (Exception e) { 
			return false;
		}
		return false;
	}
	
	/**
	 * @return The set of the ids of config extensions which are new since the last time
	 * intro was opened. May be null if there are no contributors.
	 */
	public static Set getNewContributors() {
		return newContributors;
	}
	
	/**
	 * Test to see if this contribution was newly installed
	 * @param contributionId
	 * @return
	 */
	public static boolean isNew(String contributionId) {
		if (!detectorCalled) {
			detectorCalled = true;
			new ContentDetector().isNewContentAvailable();
		}
		return newContributors != null && newContributors.contains(contributionId);
	}

}
