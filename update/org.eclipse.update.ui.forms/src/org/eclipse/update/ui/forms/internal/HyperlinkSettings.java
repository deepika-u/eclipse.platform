package org.eclipse.update.ui.forms.internal;
/*
 * (c) Copyright IBM Corp. 2000, 2001.
 * All Rights Reserved.
 */

import org.eclipse.swt.widgets.*;
import org.eclipse.swt.events.*;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.*;
import java.util.*;
import org.eclipse.swt.*;

public class HyperlinkSettings {
	public static final int UNDERLINE_NEVER = 1;
	public static final int UNDERLINE_ROLLOVER = 2;
	public static final int UNDERLINE_ALWAYS = 3;

	protected Cursor hyperlinkCursor;
	protected Cursor busyCursor;
	protected boolean hyperlinkCursorUsed=true;
	protected int hyperlinkUnderlineMode=UNDERLINE_ALWAYS;
	protected Color background;
	protected Color foreground;
	protected Color activeBackground;
	protected Color activeForeground;

public HyperlinkSettings() {
	hyperlinkCursor = new Cursor(Display.getCurrent(), SWT.CURSOR_HAND);
	busyCursor = new Cursor(Display.getCurrent(), SWT.CURSOR_WAIT);
}

public void dispose() {
	hyperlinkCursor.dispose();
	busyCursor.dispose();
}

public Color getActiveBackground() {
	return activeBackground;
}
public Color getActiveForeground() {
	return activeForeground;
}
public Color getBackground() {
	return background;
}
public Cursor getBusyCursor() {
	return busyCursor;
}
public Color getForeground() {
	return foreground;
}
public Cursor getHyperlinkCursor() {
	return hyperlinkCursor;
}
public int getHyperlinkUnderlineMode() {
	return hyperlinkUnderlineMode;
}

public boolean isHyperlinkCursorUsed() {
	return hyperlinkCursorUsed;
}

public void setActiveBackground(Color newActiveBackground) {
	activeBackground = newActiveBackground;
}
public void setActiveForeground(Color newActiveForeground) {
	activeForeground = newActiveForeground;
}
public void setBackground(Color newBackground) {
	background = newBackground;
}
public void setForeground(Color newForeground) {
	foreground = newForeground;
}
public void setHyperlinkCursorUsed(boolean newHyperlinkCursorUsed) {
	hyperlinkCursorUsed = newHyperlinkCursorUsed;
}
public void setHyperlinkUnderlineMode(int newHyperlinkUnderlineMode) {
	hyperlinkUnderlineMode = newHyperlinkUnderlineMode;
}

}
