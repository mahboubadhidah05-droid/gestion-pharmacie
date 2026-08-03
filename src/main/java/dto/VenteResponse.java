package dto;

import java.time.LocalDateTime;

/**
 * Réponse JSON d'une vente dans les consultations.
 *
 * montantRembourse / ticketModerateur : calculés automatiquement à la
 * vente si le médicament est conventionné CNAM (0 sinon).
 */
public record VenteResponse(
        int id,
        int idPharmacien,
        int idClient,
        int idMedicament,
        int quantite,
        LocalDateTime dateVente,
        double montantRembourse,
        double ticketModerateur) {
}