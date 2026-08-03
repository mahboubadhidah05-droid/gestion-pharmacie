package dto;

public record CommandeRequest(
        int idGestionnaire,
        int idMedicament,
        int quantite,
        Integer idFournisseur,
        String datePeremption) {
}