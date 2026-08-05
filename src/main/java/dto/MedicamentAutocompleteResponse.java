package dto;

/**
 * Résultat d'une recherche par autocomplétion (2+ lettres du nom) —
 * assez d'infos pour distinguer plusieurs médicaments similaires
 * (nom + dosage identiques mais forme ou fabricant différents).
 */
public record MedicamentAutocompleteResponse(
        int id,
        String nom,
        String dosage,
        String forme,
        String fabricant) {
}