package org.eclipse.kura.web.client.settings;

import org.eclipse.kura.web.client.messages.Messages;
import org.eclipse.kura.web.shared.GwtKuraErrorCode;
import org.eclipse.kura.web.shared.model.GwtSession;
import org.eclipse.kura.web.shared.service.GwtCertificatesService;
import org.eclipse.kura.web.shared.service.GwtCertificatesServiceAsync;

import com.extjs.gxt.ui.client.Style.HorizontalAlignment;
import com.extjs.gxt.ui.client.Style.Scroll;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.FormEvent;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.Info;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.button.ButtonBar;
import com.extjs.gxt.ui.client.widget.form.FormPanel;
import com.extjs.gxt.ui.client.widget.form.TextArea;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.extjs.gxt.ui.client.widget.form.FormPanel.Encoding;
import com.extjs.gxt.ui.client.widget.form.FormPanel.Method;
import com.extjs.gxt.ui.client.widget.layout.ColumnLayout;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.extjs.gxt.ui.client.widget.layout.FormData;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Label;

/**
 * This is used to display Permission Tab to revoke Permissions from Clients
 * @author AMIT KUMAR MONDAL
 *
 */
public class PermissionsTab extends LayoutContainer {

	private static final Messages MSGS = GWT.create(Messages.class);

	private final static String SERVLET_URL = "/" + GWT.getModuleName() + "/file/certificate";
	private final GwtCertificatesServiceAsync gwtCertificatesService = GWT.create(GwtCertificatesService.class);

	@SuppressWarnings("unused")
	private GwtSession			m_currentSession;
	private LayoutContainer 	m_commandInput;
	private FormPanel			m_formPanel;
	private TextArea			m_previouslyConnectedClients;
	private TextArea			m_permissionRevokedClients;

	private Button				m_executeButton;
	private Button				m_resetButton;
	private ButtonBar			m_buttonBar;


	public PermissionsTab(GwtSession currentSession) 
	{
		m_currentSession = currentSession;
	}

        
	protected void onRender(Element parent, int index) {
		super.onRender(parent, index);         
		setLayout(new FitLayout());
		setId("device-command");

		FormData formData = new FormData("95% 40%");

		//initToolBar();

		//
		// Command Form
		//
		m_formPanel = new FormPanel();
		m_formPanel.setFrame(true);
		m_formPanel.setHeaderVisible(false);
		m_formPanel.setBorders(false);
		m_formPanel.setBodyBorder(false);
		m_formPanel.setAction(SERVLET_URL);
		m_formPanel.setEncoding(Encoding.MULTIPART);
		m_formPanel.setMethod(Method.POST);
		//m_formPanel.setHeight("100.0%");

		m_formPanel.setButtonAlign(HorizontalAlignment.RIGHT);
		m_buttonBar = m_formPanel.getButtonBar();
		initButtonBar();

		//
		// Initial description
		// 
		LayoutContainer description = new LayoutContainer();
		description.setBorders(false);
		description.setLayout(new ColumnLayout());

		Label descriptionLabel = new Label(MSGS.RevokePermissionDescription());
		
		description.add(descriptionLabel);
		description.setStyleAttribute("padding-bottom", "10px");
		m_formPanel.add(description);

		//
		// Private Certificate
		//       
		m_previouslyConnectedClients = new TextArea();
		m_previouslyConnectedClients.setBorders(true);
		m_previouslyConnectedClients.setReadOnly(true);
		m_previouslyConnectedClients.setEmptyText("");
		m_previouslyConnectedClients.setName("");
		m_previouslyConnectedClients.setAllowBlank(true);
		m_previouslyConnectedClients.setFieldLabel(MSGS.clientList());
		m_formPanel.add(m_previouslyConnectedClients, formData);
		
		//
		//
		//
		m_permissionRevokedClients = new TextArea();
		m_permissionRevokedClients.setName("");
		m_permissionRevokedClients.setPassword(true);
		m_permissionRevokedClients.setAllowBlank(false);
		m_permissionRevokedClients.setEmptyText("");
		m_permissionRevokedClients.setFieldLabel(MSGS.permissionRevokedClientList());
		m_formPanel.add(m_permissionRevokedClients, new FormData("95%"));



		m_commandInput = m_formPanel;

		// Main Panel
		ContentPanel deviceCommandPanel = new ContentPanel();
		deviceCommandPanel.setBorders(false);
		deviceCommandPanel.setBodyBorder(false);
		deviceCommandPanel.setHeaderVisible(false);
		deviceCommandPanel.setScrollMode(Scroll.AUTO);
		deviceCommandPanel.setLayout(new FitLayout());
		//deviceCommandPanel.setHeight("100%");

		//deviceCommandPanel.setTopComponent(m_commandInput);
		deviceCommandPanel.add(m_commandInput);
		//deviceCommandPanel.add(m_publicCertificateArea);

		add(deviceCommandPanel);
	}

	private void initButtonBar() {
		m_executeButton = new Button(MSGS.deviceCommandExecute());
		m_executeButton.addSelectionListener(new SelectionListener<ButtonEvent>() {  
			@Override  
			public void componentSelected(ButtonEvent ce) {
				if (m_formPanel.isValid()) {
					//m_result.clear();
					m_commandInput.mask(MSGS.waiting());
					m_formPanel.submit();
				}
			}
		});

		m_resetButton = new Button(MSGS.reset());
		m_resetButton.addSelectionListener(new SelectionListener<ButtonEvent>() {  
			@Override
			public void componentSelected(ButtonEvent ce) {
				m_formPanel.reset();
			}
		});

		m_buttonBar.add(m_resetButton);
		m_buttonBar.add(m_executeButton);
	}

	public void refresh() {
		m_commandInput.unmask();
	}

}
