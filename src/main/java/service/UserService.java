package service;

import java.util.logging.Level;
import java.util.logging.Logger;

import dao.UserDAO;

public final class UserService {

    private static final Logger LOGGER =
            Logger.getLogger(UserService.class.getName());

    private static final UserDAO DAO = new UserDAO();

    private UserService() {
        // Classe utilitaire : empêche l'instanciation
    }

    public static void consulterProfil(String login) {

        String[] profil = DAO.getProfil(login);

        if (profil != null && profil.length >= 2) {

            LOGGER.info("=== Profil Utilisateur ===");

            LOGGER.log(Level.INFO,
                    "Nom : {0}",
                    profil[0]);

            LOGGER.log(Level.INFO,
                    "Prénom : {0}",
                    profil[1]);

        } else {

            LOGGER.warning("Utilisateur introuvable !");
        }
    }
}