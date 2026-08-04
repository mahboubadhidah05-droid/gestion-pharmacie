package dto;

/**
 * Corps JSON de POST /api/clients.
 * numeroCnam et cin sont optionnels (null si non renseignés) — mais
 * le cin, quand il est fourni, doit être unique (contrainte en base).
 */
public record ClientRequest(
        String nom,
        String prenom,
        String email,
        String adresse,
        String numeroCnam,
        String cin) {
}