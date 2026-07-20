package dto;

/**
 * Réponse JSON de GET /api/medicaments/{id}/stock.
 */
public record StockResponse(
        int id,
        int stock) {
}
