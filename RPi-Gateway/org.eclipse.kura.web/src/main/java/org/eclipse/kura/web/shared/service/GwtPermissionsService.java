package org.eclipse.kura.web.shared.service;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

/**
 * Used to save the detailed permissions
 * @author AMIT KUMAR MONDAL
 *
 */
@RemoteServiceRelativePath("permissions")
public interface GwtPermissionsService extends RemoteService {
	
	public boolean saveToPermissionsFile(String data);
	public String retrievePermissionFileData();

}
