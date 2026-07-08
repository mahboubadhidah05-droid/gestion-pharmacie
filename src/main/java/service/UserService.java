package service;

import First_project.UserDAO;

public class UserService {

    private final UserDAO dao;

    public UserService(UserDAO dao) {
        this.dao = dao;
    }

    public void consulterProfil(String login) {
        String[] profil = dao.getProfil(login);
        if (profil != null) {
            System.out.println("\n=== Profil Utilisateur ===");
            System.out.println("Nom : " + profil[0]);
            System.out.println("Prénom : " + profil[1]);
            System.out.println("=========================\n");
        } else {
            System.out.println("Utilisateur introuvable !");
        }
    }
}
