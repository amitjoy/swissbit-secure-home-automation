package org.eclipse.kura.web.client.settings;

import java.util.ArrayList;
import java.util.List;

public class TestDataFactory {
	
	public static List<Client> getClients() {
		List<Client> clients = new ArrayList<Client>();
		Client client1 = new Client("12345678", true);
		Client client2 = new Client("21345678", true);
		Client client3 = new Client("32345678", true);
		Client client4 = new Client("46345678", false);
		Client client5 = new Client("72345678", true);
		Client client6 = new Client("52345678", false);
		Client client7 = new Client("82345678", true);
		clients.add(client1);
		clients.add(client2);
		clients.add(client3);
		clients.add(client4);
		clients.add(client5);
		clients.add(client6);
		clients.add(client7);
		return clients;
	}

}
