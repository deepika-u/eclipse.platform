package org.eclipse.team.internal.ccvs.core.response.custom;

/*
 * (c) Copyright IBM Corp. 2000, 2001.
 * All Rights Reserved.
 */
 
import org.eclipse.team.internal.ccvs.core.resources.RemoteFile;
import org.eclipse.team.internal.ccvs.core.resources.RemoteFileRevision;
import org.eclipse.core.runtime.PlatformObject;
import org.eclipse.team.ccvs.core.ILogEntry;
import org.eclipse.team.ccvs.core.IRemoteFile;

public class LogEntry extends PlatformObject implements ILogEntry {

	private RemoteFileRevision file;
	private String author;
	private String date;
	private String comment;
	private String state;
	private CVSTag[] tags;

	public LogEntry(RemoteFile file, String revision, String author, String date, String comment, String state, CVSTag[] tags) {
		this.file = file.toRemoteFileRevision(revision);
		this.author = author;
		this.date = date;
		this.comment = comment;
		this.state = state;
		this.tags = tags;
	}
	
	/**
	 * @see ILogEntry#getRevision()
	 */
	public String getRevision() {
		return file.getRevision();
	}

	/**
	 * @see ILogEntry#getAuthor()
	 */
	public String getAuthor() {
		return author;
	}

	/**
	 * @see ILogEntry#getDate()
	 */
	public String getDate() {
		return date;
	}

	/**
	 * @see ILogEntry#getComment()
	 */
	public String getComment() {
		return comment;
	}

	/**
	 * @see ILogEntry#getState()
	 */
	public String getState() {
		return state;
	}

	/**
	 * @see ILogEntry#getTags()
	 */
	public String[] getTags() {
		String[] tagNames = new String[tags.length];
		for (int i=0;i<tags.length;i++)
			tagNames[i] = tags[i].getName();
		return tagNames;
	}

	/**
	 * @see ILogEntry#getRemoteFile()
	 */
	public IRemoteFile getRemoteFile() {
		return file;
	}

}

