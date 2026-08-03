package dto;

public record UtilisateurCreationRequest(
        String nom,
        String prenom,
        String login,
        String pwd,
        String role) {
}