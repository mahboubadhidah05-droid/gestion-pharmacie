package service;

import dao.ClientDAO;

public class ClientService {

    private final ClientDAO clientDAO;


    public ClientService(ClientDAO clientDAO) {
        this.clientDAO = clientDAO;
    }


    public void creerClient(
            String nom,
            String prenom,
            String email,
            String adresse) {

        clientDAO.ajouterClient(
                nom,
                prenom,
                email,
                adresse
        );
    }
}