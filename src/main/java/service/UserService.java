package service;

import java.util.logging.Level;
import java.util.logging.Logger;

import dao.UserDAO;

public class UserService {

    private static final Logger LOGGER =
            Logger.getLogger(UserService.class.getName());

    private static final String TITRE_PROFIL =
            "=== Profil Utilisateur ===";

    private static final String UTILISATEUR_INEXISTANT =
            "Utilisateur introuvable !";


    private final UserDAO dao;


    public UserService(UserDAO dao) {
        this.dao = dao;
    }


    public void consulterProfil(String login) {

        String[] profil =
                dao.getProfil(login);


        if (profilValide(profil)) {

            afficherProfil(profil);

        } else {

            LOGGER.warning(UTILISATEUR_INEXISTANT);
        }
    }


    /**
     * Retourne le profil brut {nom, prenom} pour la couche web
     * (tableau vide si l'utilisateur est introuvable).
     */
    public String[] getProfil(String login) {
        return dao.getProfil(login);
    }


    private boolean profilValide(String[] profil) {

        return profil != null
                && profil.length >= 2;
    }


    private void afficherProfil(String[] profil) {

        LOGGER.info(TITRE_PROFIL);


        LOGGER.log(
                Level.INFO,
                "Nom : {0}",
                profil[0]
        );


        LOGGER.log(
                Level.INFO,
                "Prénom : {0}",
                profil[1]
        );
    }
}