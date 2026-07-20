package dto;

/**
 * Réponse JSON de GET /api/medicaments/{id}/stock-critique.
 */
public record StockCritiqueResponse(
        boolean critique,
        String message) {
}
