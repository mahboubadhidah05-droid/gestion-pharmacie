package dto;

import java.time.LocalDateTime;

/**
 * Réponse JSON d'une vente dans les consultations.
 */
public record VenteResponse(
        int id,
        int idPharmacien,
        int idClient,
        int idMedicament,
        int quantite,
        LocalDateTime dateVente) {
}