package dto;

/**
 * Réponse JSON après création d'un client.
 */
public record ClientResponse(
        int id,
        String message) {
}