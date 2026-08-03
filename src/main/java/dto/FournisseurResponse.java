package dto;

public record FournisseurResponse(
        int id,
        String nom,
        String telephone,
        String email,
        String adresse) {
}