package dto;

/**
 * Sous-ensemble des infos d'un médicament nécessaires au moment
 * d'une vente pour calculer le remboursement CNAM, sans avoir à
 * exposer tout le MedicamentResponse à VenteService.
 */
public record MedicamentVenteInfo(
        double prix,
        boolean conventionneCnam,
        double tauxRemboursement) {
}