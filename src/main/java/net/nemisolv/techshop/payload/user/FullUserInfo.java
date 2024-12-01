package net.nemisolv.techshop.payload.user;

public record FullUserInfo(
        Long id,
        String username,
        String email,
        String firstName,
        String lastName,
        boolean emailVerified,
        String imgUrl,
        String role,
        String authProvider
) {
}
