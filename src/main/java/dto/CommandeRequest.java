package dto;

/**
 * Corps JSON de POST /api/commandes.
 */
public record CommandeRequest(
        int idGestionnaire,
        int idMedicament,
        int quantite) {
}