package dto;

/**
 * Réponse JSON de POST /api/clients.
 */
public record ClientResponse(
        int id,
        String message) {
}