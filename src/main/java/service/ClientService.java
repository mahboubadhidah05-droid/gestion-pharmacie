package service;

import java.util.logging.Logger;

import dao.ClientDAO;

public class ClientService {

    private static final Logger LOGGER =
            Logger.getLogger(ClientService.class.getName());

    private final ClientDAO clientDAO;


    public ClientService(ClientDAO clientDAO) {
        this.clientDAO = clientDAO;
    }


    public void creerClient(String nom, String prenom, String email, String adresse) {

        clientDAO.ajouterClient(
                nom,
                prenom,
                email,
                adresse
        );

        LOGGER.info("Client créé avec succès !");
    }
}