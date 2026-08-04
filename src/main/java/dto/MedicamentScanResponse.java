package dto;

/**
 * Réponse d'une recherche par code-barres — juste assez d'infos pour
 * confirmer visuellement le produit trouvé et pré-remplir une vente.
 */
public record MedicamentScanResponse(
        int id,
        String nom,
        String dosage,
        int stock) {
}