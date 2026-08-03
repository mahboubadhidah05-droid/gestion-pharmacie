package dto;

public record CnamRapportResponse(
        String dateDebut,
        String dateFin,
        int nombreVentesConcernees,
        double totalMontantRembourse,
        double totalTicketModerateur) {
}