package dto;

public record LotResponse(
        int id,
        int idMedicament,
        int quantite,
        String datePeremption) {
}
