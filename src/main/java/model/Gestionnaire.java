package model;

/**
 * Classe représentant un gestionnaire.
 * Hérite également de la classe Utilisateur.
 */
public class Gestionnaire extends Utilisateur {

    public Gestionnaire(int id, String nom, String prenom,
                        String login, String password) {
        super(id, nom, prenom, login, password);
    }
}

