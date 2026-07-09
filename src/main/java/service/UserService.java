package service;

import java.util.logging.Logger;
import dao.UserDAO;

public class UserService {

    private static final Logger LOGGER =
            Logger.getLogger(UserService.class.getName());

    private static UserDAO dao = new UserDAO();

    public UserService(UserDAO dao) {
        UserService.dao = dao;
    }

    public static void consulterProfil(String login) {

        String[] profil = dao.getProfil(login);

        if (profil != null && profil.length >= 2) {

            LOGGER.info("=== Profil Utilisateur ===");
            LOGGER.info(String.format("Nom : %s", profil[0]));
            LOGGER.info(String.format("Prénom : %s", profil[1]));

        } else {
            LOGGER.warning("Utilisateur introuvable !");
        }
    }
}