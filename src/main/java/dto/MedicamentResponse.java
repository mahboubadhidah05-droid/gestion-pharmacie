package dto;

/**
 * Réponse JSON d'un médicament dans GET /api/medicaments.
 */
public record MedicamentResponse(
        int id,
        String nom,
        String dosage,
        int stock,
        double prix,
        int seuilCritique) {
}