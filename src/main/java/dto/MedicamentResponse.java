package dto;

/**
 * Réponse JSON d'un médicament dans GET /api/medicaments.
 *
 * quantitePerimee : nombre d'unités réellement périmées parmi le stock
 * total (0 si aucune) — permet de distinguer "tout le stock est périmé"
 * de "une partie seulement l'est", plutôt qu'une simple date ambiguë.
 *
 * conventionneCnam / tauxRemboursement : indiquent si ce médicament
 * est pris en charge par la CNAM et à quel taux (ex : 0.7 = 70%),
 * utilisés pour calculer le remboursement à la vente.
 *
 * codeBarre : facultatif (null si non renseigné), utilisé pour la
 * recherche rapide par scan lors d'une vente.
 *
 * forme / fabricant : facultatifs, utilisés pour distinguer plusieurs
 * médicaments qui partagent le même nom + dosage (ex : comprimé vs
 * sirop) dans la recherche par autocomplétion.
 */
public record MedicamentResponse(
        int id,
        String nom,
        String dosage,
        int stock,
        double prix,
        int seuilCritique,
        String datePeremption,
        int quantitePerimee,
        boolean conventionneCnam,
        double tauxRemboursement,
        String codeBarre,
        String forme,
        String fabricant) {
}