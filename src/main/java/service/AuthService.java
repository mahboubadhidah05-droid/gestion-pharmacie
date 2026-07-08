package service;

import First_project.UtilisateurDAO;

/**
 * Adaptation nécessaire dans votre code appelant :
 * avant : AuthService.login(login, pwd);
 * après : new AuthService(new UtilisateurDAO()).login(login, pwd);
 */
public class AuthService {

    private final UtilisateurDAO dao;

    public AuthService(UtilisateurDAO dao) {
        this.dao = dao;
    }

    public String login(String login, String pwd) {
        return dao.getRole(login, pwd);
    }
}
