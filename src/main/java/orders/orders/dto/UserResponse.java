package orders.orders.dto;

import orders.orders.model.User;

import java.time.LocalDateTime;

public record UserResponse(
        long id,
        String email,
        String username,
        boolean enabled,
        LocalDateTime createdAt,
        LocalDateTime updatedAt) {
    public static UserResponse from(User user) {
        return new UserResponse(user.getId(),  user.getEmail(), user.getUsername(), user.isEnabled(), user.getCreatedAt(), user.getUpdatedAt());
    }
}
