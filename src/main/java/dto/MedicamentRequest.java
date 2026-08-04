package dto;

/**
 * DTO représentant les données envoyées pour créer un médicament.
 * codeBarre est facultatif (null si non renseigné).
 */
public record MedicamentRequest(
        String nom,
        String dosage,
        int stock,
        double prix,
        int seuil,
        String datePeremption,
        boolean conventionneCnam,
        double tauxRemboursement,
        String codeBarre) {
}