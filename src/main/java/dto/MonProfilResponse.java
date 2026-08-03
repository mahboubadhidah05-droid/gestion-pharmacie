package dto;

public record MonProfilResponse(
        String nom,
        String prenom,
        String login,
        String email) {
}