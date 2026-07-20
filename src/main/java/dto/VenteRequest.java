package dto;

/**
 * Corps JSON de POST /api/ventes.
 */
public record VenteRequest(
        int idPharmacien,
        int idClient,
        int idMedicament,
        int quantite) {
}