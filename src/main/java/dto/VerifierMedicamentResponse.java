package dto;

/**
 * Réponse consolidée pour "Vérifier un médicament" — combine en une
 * seule recherche le statut de stock critique et de péremption,
 * pratique pour une pharmacie avec un grand catalogue où chercher
 * chaque information séparément serait pénible.
 */
public record VerifierMedicamentResponse(
        int id,
        String nom,
        String dosage,
        int stock,
        boolean stockCritique,
        int quantitePerimee,
        String datePeremptionLaPlusProche,
        boolean bientotPerime) {
}