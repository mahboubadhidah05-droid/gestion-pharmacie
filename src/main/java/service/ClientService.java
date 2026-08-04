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
            String adresse,
            String numeroCnam,
            String cin) {

        return clientDAO.ajouterClient(
                nom,
                prenom,
                email,
                adresse,
                numeroCnam,
                cin
        );
    }


    public boolean existeClient(int idClient) {
        return clientDAO.existeClient(idClient);
    }
}