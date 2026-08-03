package service;

import java.util.List;

import dao.UtilisateurGestionDAO;
import dto.UtilisateurGestionResponse;
import utils.MotDePasseUtils;

public class UtilisateurGestionService {

    public static final String ROLE_PHARMACIEN = "PHARMACIEN";
    public static final String ROLE_GESTIONNAIRE = "GESTIONNAIRE";

    private final UtilisateurGestionDAO dao;

    public UtilisateurGestionService(UtilisateurGestionDAO dao) {
        this.dao = dao;
    }

    public List<UtilisateurGestionResponse> listerTous() {
        return dao.listerTous();
    }

    /**
     * @return l'ID créé, ou un code d'erreur négatif :
     *         -1 rôle invalide, -2 mot de passe trop faible,
     *         -3 login déjà utilisé.
     */
    public int creer(
            String nom,
            String prenom,
            String login,
            String pwd,
            String role) {

        if (!estRoleValide(role)) {
            return -1;
        }

        if (!MotDePasseUtils.respecteReglesBasiques(pwd)) {
            return -2;
        }

        if (dao.loginExiste(login)) {
            return -3;
        }

        String pwdHache = MotDePasseUtils.hacher(pwd);

        return dao.creerUtilisateur(nom, prenom, login, pwdHache, role);
    }

    /**
     * @return 0 succès, -1 rôle invalide, -2 mot de passe trop faible
     *         (uniquement si un nouveau mot de passe est fourni),
     *         -4 utilisateur introuvable.
     */
    public int modifier(
            int id,
            String role,
            String nom,
            String prenom,
            String login,
            String pwd) {

        if (!estRoleValide(role)) {
            return -1;
        }

        String pwdHache = null;

        if (pwd != null && !pwd.isBlank()) {

            if (!MotDePasseUtils.respecteReglesBasiques(pwd)) {
                return -2;
            }

            pwdHache = MotDePasseUtils.hacher(pwd);
        }

        boolean modifie =
                dao.modifierUtilisateur(
                        id,
                        role,
                        nom,
                        prenom,
                        login,
                        pwdHache
                );

        return modifie ? 0 : -4;
    }

    public boolean supprimer(int id, String role) {

        if (!estRoleValide(role)) {
            return false;
        }

        return dao.supprimerUtilisateur(id, role);
    }

    private boolean estRoleValide(String role) {

        return ROLE_PHARMACIEN.equals(role)
                || ROLE_GESTIONNAIRE.equals(role);
    }
}