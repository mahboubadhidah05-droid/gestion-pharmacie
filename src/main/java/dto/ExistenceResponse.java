package dto;

/**
 * Réponse JSON de GET /api/clients/{id}/existe.
 */
public record ExistenceResponse(
        boolean existe) {
}