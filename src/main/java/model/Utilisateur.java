package model;

/**
 * Classe mère représentant un utilisateur de l'application.
 * Elle est héritée par Pharmacien et Gestionnaire.
 */
public class Utilisateur {

    protected int id;
    protected String nom;
    protected String prenom;
    protected String login;
    protected String password;

    public Utilisateur(int id, String nom, String prenom,
                       String login, String password) {
        this.id = id;
        this.nom = nom;
        this.prenom = prenom;
        this.login = login;
        this.password = password;
    }
}

