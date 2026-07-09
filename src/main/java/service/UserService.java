package service;

import java.util.logging.Logger;

import dao.UserDAO;

public final class UserService {

    private static final Logger LOGGER = Logger.getLogger(UserService.class.getName());

    private static final UserDAO DAO = new UserDAO();

    private UserService() {
        throw new IllegalStateException("Utility class");
    }

    public static void consulterProfil(String login) {

        String[] profil = DAO.getProfil(login);

        if (profil != null && profil.length >= 2) {

            LOGGER.info("\n=== Profil Utilisateur ===");
            LOGGER.info("Nom : " + profil[0]);
            LOGGER.info("Prénom : " + profil[1]);
            LOGGER.info("=========================\n");

        } else {

            LOGGER.warning("Utilisateur introuvable !");
        }
    }
}