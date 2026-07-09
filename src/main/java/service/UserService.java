package service;

import dao.UserDAO;

public class UserService {

    private static UserDAO dao = new UserDAO();

    public UserService(UserDAO dao) {
        UserService.dao = dao;
    }

    public static void consulterProfil(String login) {
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
