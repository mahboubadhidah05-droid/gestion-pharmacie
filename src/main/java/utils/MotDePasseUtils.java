package utils;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

/**
 * Utilitaire de gestion des mots de passe :
 * - hachage (BCrypt) pour les nouveaux mots de passe ;
 * - vérification compatible avec les anciens mots de passe en clair
 *   (transition en douceur, aucun compte existant n'est cassé) ;
 * - règle de complexité basique (8 caractères min, lettre + chiffre).
 */
public final class MotDePasseUtils {

    private static final BCryptPasswordEncoder ENCODEUR =
            new BCryptPasswordEncoder();

    private static final int LONGUEUR_MIN = 8;

    private MotDePasseUtils() {
        // Classe utilitaire : empêche l'instanciation
    }

    /**
     * Hache un mot de passe en clair pour le stockage.
     */
    public static String hacher(String motDePasseClair) {
        return ENCODEUR.encode(motDePasseClair);
    }

    /**
     * Vérifie un mot de passe saisi contre la valeur stockée en base,
     * que celle-ci soit un hash BCrypt (nouveaux comptes) ou du texte
     * en clair (anciens comptes, avant migration).
     */
    public static boolean correspond(
            String motDePasseClair,
            String valeurStockee) {

        if (motDePasseClair == null || valeurStockee == null) {
            return false;
        }

        if (estHache(valeurStockee)) {
            return ENCODEUR.matches(motDePasseClair, valeurStockee);
        }

        return motDePasseClair.equals(valeurStockee);
    }

    /**
     * Détecte si une valeur est déjà un hash BCrypt
     * (préfixes standards $2a$ / $2b$ / $2y$).
     */
    public static boolean estHache(String valeur) {

        return valeur != null
                && (valeur.startsWith("$2a$")
                        || valeur.startsWith("$2b$")
                        || valeur.startsWith("$2y$"));
    }

    /**
     * Règle basique universelle : au moins 8 caractères,
     * au moins une lettre et un chiffre.
     */
    public static boolean respecteReglesBasiques(String motDePasseClair) {

        if (motDePasseClair == null
                || motDePasseClair.length() < LONGUEUR_MIN) {
            return false;
        }

        boolean contientLettre =
                motDePasseClair.chars().anyMatch(Character::isLetter);

        boolean contientChiffre =
                motDePasseClair.chars().anyMatch(Character::isDigit);

        return contientLettre && contientChiffre;
    }
}