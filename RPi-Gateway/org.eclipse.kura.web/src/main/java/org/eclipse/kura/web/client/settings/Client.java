package org.eclipse.kura.web.client.settings;

import java.util.Date;

/**
 * 
 * @author AMIT KUMAR MONDAL
 *
 */
public final class Client {

	private String id;
	public Client(String id, boolean isAllowed) {
		super();
		this.id = id;
		this.isAllowed = isAllowed;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	private boolean isAllowed;
	private Date dateModified;

	public Date getDateModified() {
		return dateModified;
	}

	public void setDateModified(Date dateModified) {
		this.dateModified = dateModified;
	}

	public boolean isAllowed() {
		return isAllowed;
	}

	public void setAllowed(boolean isAllowed) {
		this.isAllowed = isAllowed;
	}

}
