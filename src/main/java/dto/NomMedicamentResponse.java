package dto;

/**
 * Version allégée d'un médicament, pour les listes déroulantes
 * accessibles aux deux rôles — expose uniquement le nom et le dosage,
 * jamais le stock, le prix ni le seuil critique (données de gestion
 * réservées au Gestionnaire).
 */
public record NomMedicamentResponse(
        String nom,
        String dosage) {
}