package model;

/**
 * Classe représentant un pharmacien.
 * Hérite de la classe Utilisateur.
 */
public class Pharmacien extends Utilisateur {

    public Pharmacien(int id, String nom, String prenom,
                      String login, String password) {
        super(id, nom, prenom, login, password);
    }
}
