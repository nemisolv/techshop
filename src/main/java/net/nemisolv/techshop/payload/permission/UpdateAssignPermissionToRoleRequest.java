package net.nemisolv.techshop.payload.permission;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record UpdateAssignPermissionToRoleRequest(
        @NotNull
        @NotEmpty(message = "Permission ids must not be empty")
        List<Long> permissionIds
) {
}
