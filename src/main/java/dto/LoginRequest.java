package dto;

/**
 * Corps JSON attendu par POST /api/auth/login.
 */
public record LoginRequest(
        String login,
        String pwd) {
}