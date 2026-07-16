package dto;

import java.sql.Timestamp;

public record StockHistoriqueResponse(
        int idMedicament,
        int quantite,
        Timestamp dateModification) {
}