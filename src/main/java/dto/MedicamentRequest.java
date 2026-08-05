package dto;

/**
 * DTO représentant les données envoyées pour créer un médicament.
 * codeBarre, forme et fabricant sont facultatifs (null si non renseignés).
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
        String codeBarre,
        String forme,
        String fabricant) {
}