package service;

import java.util.logging.Logger;
import dao.UserDAO;

public class UserService {

    private static final Logger LOGGER =
            Logger.getLogger(UserService.class.getName());

    private final UserDAO dao;

    public UserService(UserDAO dao) {
        this.dao = dao;
    }

    public void consulterProfil(String login) {

        String[] profil = dao.getProfil(login);

        if (profil != null && profil.length >= 2) {

            LOGGER.info("=== Profil Utilisateur ===");

            LOGGER.log(
                    java.util.logging.Level.INFO,
                    "Nom : {0}",
                    profil[0]
            );

            LOGGER.log(
                    java.util.logging.Level.INFO,
                    "Prénom : {0}",
                    profil[1]
            );

        } else {

            LOGGER.warning("Utilisateur introuvable !");
        }
    }
}