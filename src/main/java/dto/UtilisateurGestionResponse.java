package dto;

public record UtilisateurGestionResponse(
        int id,
        String nom,
        String prenom,
        String login,
        String role) {
}