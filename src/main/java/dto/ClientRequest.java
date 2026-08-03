package dto;

/**
 * Corps JSON de POST /api/clients.
 * numeroCnam est optionnel (null si le client n'est pas assuré CNAM
 * ou ne l'a pas renseigné).
 */
public record ClientRequest(
        String nom,
        String prenom,
        String email,
        String adresse,
        String numeroCnam) {
}