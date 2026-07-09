package service;

import dao.UtilisateurDAO;

public class AuthService {

    private static UtilisateurDAO dao = new UtilisateurDAO();

    public AuthService(UtilisateurDAO dao) {
        AuthService.dao = dao;
    }

    public static String login(String login, String pwd) {
        return dao.getRole(login, pwd);
    }

    public static UtilisateurDAO getDao() {
        return dao;
    }
}