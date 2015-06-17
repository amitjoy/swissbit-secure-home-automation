package org.eclipse.kura.web.server;

import org.eclipse.kura.web.server.util.ServiceLocator;
import org.eclipse.kura.web.shared.service.GwtPermissionsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.swissbit.accesscontrol.IAccessControl;

/**
 * 
 * @author AMIT KUMAR MONDAL
 *
 */
public class GwtPermissionsServiceImpl extends OsgiRemoteServiceServlet implements GwtPermissionsService {
	
	private static final long serialVersionUID = 8273190432964194616L;
	
	private static Logger s_logger = LoggerFactory.getLogger(GwtPermissionsServiceImpl.class);
	
	public boolean saveToPermissionsFile(String data) {
		IAccessControl m_accessControl = null;
		try {
			m_accessControl = ServiceLocator.getInstance().getService(IAccessControl.class);
			if (m_accessControl != null)
				m_accessControl.savePermission(data);
			return true;
		} catch (Throwable t) {
			s_logger.warn("Exception: {}", t.toString());
		}
		return false;
	}

	public String retrievePermissionFileData() {
		IAccessControl m_accessControl = null;
		try {
			m_accessControl = ServiceLocator.getInstance().getService(IAccessControl.class);
			if (m_accessControl != null) {
				return m_accessControl.readPermission();
			}
				
		} catch (Throwable t) {
			s_logger.warn("Exception: {}", t.toString());
		}
		return null;
	}

}
