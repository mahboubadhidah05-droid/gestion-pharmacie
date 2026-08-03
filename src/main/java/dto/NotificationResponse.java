package dto;

import java.sql.Timestamp;

public record NotificationResponse(
        int id,
        String message,
        Timestamp dateCreation,
        boolean lue) {
}