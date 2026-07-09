package service;

import dao.UtilisateurDAO;

public final class AuthService {

    private static final UtilisateurDAO DAO = new UtilisateurDAO();

    private AuthService() {
        // Classe utilitaire : empêche l'instanciation
    }

    public static String login(String login, String pwd) {
        return DAO.getRole(login, pwd);
    }

    public static UtilisateurDAO getDao() {
        return DAO;
    }
}