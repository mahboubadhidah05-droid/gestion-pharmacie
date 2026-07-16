package dto;

/**
 * DTO représentant les données envoyées pour créer un médicament.
 */
public record MedicamentRequest(
        String nom,
        String dosage,
        int stock,
        double prix,
        int seuil) {
}