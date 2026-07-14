package service;

import dao.UtilisateurDAO;

public class AuthService {

    private final UtilisateurDAO dao;


    public AuthService(UtilisateurDAO dao) {
        this.dao = dao;
    }


    public String login(
            String login,
            String pwd) {

        return dao.getRole(
                login,
                pwd
        );
    }
}