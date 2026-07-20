package service;

import dao.ClientDAO;

public class ClientService {

    private final ClientDAO clientDAO;


    public ClientService(ClientDAO clientDAO) {
        this.clientDAO = clientDAO;
    }


    public int creerClient(
            String nom,
            String prenom,
            String email,
            String adresse) {

        return clientDAO.ajouterClient(
                nom,
                prenom,
                email,
                adresse
        );
    }


    public boolean existeClient(int idClient) {
        return clientDAO.existeClient(idClient);
    }
}