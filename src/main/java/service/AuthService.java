package service;

import dao.UtilisateurDAO;

/**
 * Adaptation nécessaire dans votre code appelant :
 * avant : AuthService.login(login, pwd);
 * après : new AuthService(new UtilisateurDAO()).login(login, pwd);
 */
public class AuthService {

    private static UtilisateurDAO dao = new UtilisateurDAO();

    public AuthService(UtilisateurDAO dao) {
        AuthService.dao = dao;
    }

    public static String login(String login, String pwd) {
        return getDao().getRole(login, pwd);
    }

	public static UtilisateurDAO getDao() {
		return dao;
	}
}
