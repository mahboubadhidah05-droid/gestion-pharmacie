package dto;

/**
 * Corps JSON de POST /api/clients.
 */
public record ClientRequest(
        String nom,
        String prenom,
        String email,
        String adresse) {
}