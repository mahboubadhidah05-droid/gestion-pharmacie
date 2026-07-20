package dto;

/**
 * Réponse JSON de GET /api/utilisateurs/profil.
 */
public record ProfilResponse(
        String login,
        String nom,
        String prenom,
        String role) {
}