package dto;

public record ChangerMotDePasseRequest(
        String pwdActuel,
        String pwdNouveau) {
}