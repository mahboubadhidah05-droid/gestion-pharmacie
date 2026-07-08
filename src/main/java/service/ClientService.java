package service;

import First_project.ClientDAO;

/**
 * Adaptation nécessaire dans votre code appelant :
 * avant : ClientService.creerClient(...);
 * après : new ClientService(new ClientDAO()).creerClient(...);
 */
public class ClientService {

    private final ClientDAO clientDAO;

    public ClientService(ClientDAO clientDAO) {
        this.clientDAO = clientDAO;
    }

    public void creerClient(String nom, String prenom, String email, String adresse) {
        clientDAO.ajouterClient(nom, prenom, email, adresse);
        System.out.println("✅ Client créé avec succès !");
    }
}