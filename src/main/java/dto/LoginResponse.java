package dto;

/**
 * Réponse JSON renvoyée après connexion (ou par /api/auth/me).
 */
public record LoginResponse(
        String login,
        String role) {
}