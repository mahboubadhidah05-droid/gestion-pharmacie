package dto;

public record UtilisateurModificationRequest(
        String nom,
        String prenom,
        String login,
        String pwd) {
}