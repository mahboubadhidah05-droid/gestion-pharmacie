package dto;

public record FournisseurRequest(
        String nom,
        String telephone,
        String email,
        String adresse) {
}