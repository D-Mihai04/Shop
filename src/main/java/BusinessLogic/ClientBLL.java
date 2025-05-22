package BusinessLogic;

import DataAccess.ClientDAO;
import Model.Client;

import java.util.List;


/**
 * ClientBLL implements the logic for client management.
 * It manages the adding editing and deleting operations by using the methods in clientDAO.
 */
public class ClientBLL {
    private final ClientDAO clientDAO;

    public ClientBLL() {
        clientDAO = new ClientDAO();
    }

    public List<Client> getAllClients() {
        return clientDAO.findAll();
    }

    public Client insertClient(Client client) {
        return clientDAO.insert(client);
    }

    public Client updateClient(Client client) {
        return clientDAO.update(client);
    }

    public void deleteClient(int id) {
        clientDAO.delete(id);
    }


}
