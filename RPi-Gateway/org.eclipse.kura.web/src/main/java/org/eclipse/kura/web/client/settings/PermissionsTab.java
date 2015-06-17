package org.eclipse.kura.web.client.settings;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import org.apache.commons.io.IOUtils;
import org.eclipse.kura.web.shared.model.GwtSession;
import org.eclipse.kura.web.shared.service.GwtCertificatesService;
import org.eclipse.kura.web.shared.service.GwtPermissionsService;

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
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.http.client.Response;
import org.eclipse.kura.web.shared.service.GwtPermissionsServiceAsync;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.eclipse.kura.web.client.messages.Messages;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Label;

/**
 * This is used to display Permission Tab to revoke Permissions from Clients
 * 
 * @author AMIT KUMAR MONDAL
 *
 */
public class PermissionsTab extends LayoutContainer {

	private static final Messages MSGS = GWT.create(Messages.class);
	private final GwtPermissionsServiceAsync m_gwtPermissionsService = GWT.create(GwtPermissionsService.class);

	private GwtSession m_currentSession;
	private LayoutContainer m_commandInput;
	private FormPanel m_formPanel;
	private TextArea m_previouslyConnectedClients;
	private TextArea m_permissionRevokedClients;

	private Button m_saveButton;
	private Button m_resetButton;
	private ButtonBar m_buttonBar;
	
	private String m_clientsList = "-- NO PERMISSION --";

	public PermissionsTab(GwtSession currentSession) {
		m_currentSession = currentSession;
	}

	protected void onRender(Element parent, int index) {
		super.onRender(parent, index);
		setLayout(new FitLayout());
		setId("client-permissions");

		FormData formData = new FormData("95% 40%");

		m_formPanel = new FormPanel();
		m_formPanel.setFrame(true);
		m_formPanel.setHeaderVisible(false);
		m_formPanel.setBorders(false);
		m_formPanel.setBodyBorder(false);

		m_formPanel.setButtonAlign(HorizontalAlignment.RIGHT);
		m_buttonBar = m_formPanel.getButtonBar();
		initButtonBar();

		LayoutContainer description = new LayoutContainer();
		description.setBorders(false);
		description.setLayout(new ColumnLayout());

		Label descriptionLabel = new Label(MSGS.RevokePermissionDescription());

		description.add(descriptionLabel);
		description.setStyleAttribute("padding-bottom", "10px");
		m_formPanel.add(description);

		m_previouslyConnectedClients = new TextArea();
		m_previouslyConnectedClients.setBorders(true);
		m_previouslyConnectedClients.setReadOnly(true);
		m_previouslyConnectedClients.setEmptyText("");
		m_previouslyConnectedClients.setName("");
		
		m_gwtPermissionsService.retrievePermissionFileData(new AsyncCallback<String>() {

			public void onFailure(Throwable caught) {
				Info.display(MSGS.error(), caught.getLocalizedMessage());
			}

			public void onSuccess(String result) {
				m_clientsList = result;
				Info.display(MSGS.info(), "Data Retrieved");
			}
			
		});
		
		m_previouslyConnectedClients.setValue(m_clientsList);
		m_previouslyConnectedClients.setAllowBlank(true);
		m_previouslyConnectedClients.setFieldLabel(MSGS.clientList());
		m_formPanel.add(m_previouslyConnectedClients, formData);

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

		deviceCommandPanel.add(m_commandInput);

		add(deviceCommandPanel);
	}

	private void initButtonBar() {
		m_saveButton = new Button(MSGS.saveRevokedClients());
		m_saveButton.addSelectionListener(new SelectionListener<ButtonEvent>() {
			@Override
			public void componentSelected(ButtonEvent ce) {
				if (m_formPanel.isValid()) {
					// m_result.clear();
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
		m_buttonBar.add(m_saveButton);
	}

	public void refresh() {
		m_commandInput.unmask();
	}

}
