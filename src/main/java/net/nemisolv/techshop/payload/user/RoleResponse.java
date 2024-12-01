package net.nemisolv.techshop.payload.user;

import java.util.Set;

public record RoleResponse(
        Long id,
        String name,
        Set<String> permissions
) {
}
