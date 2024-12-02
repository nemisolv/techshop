package net.nemisolv.techshop.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import net.nemisolv.techshop.payload.ApiResponse;
import net.nemisolv.techshop.payload.PagedResponse;
import net.nemisolv.techshop.payload.QueryOption;
import net.nemisolv.techshop.payload.permission.AssignPermissionToRoleRequest;
import net.nemisolv.techshop.payload.permission.PermissionResponse;
import net.nemisolv.techshop.payload.permission.UpdateAssignPermissionToRoleRequest;
import net.nemisolv.techshop.service.PermissionService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RequestMapping("/api/v1/permissions")
@RestController
public class PermissionController {

    private final PermissionService permissionService;

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ApiResponse<PagedResponse<PermissionResponse>> getPermissions(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int limit,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDirection,
            @RequestParam(defaultValue = "") String searchQuery
    ) {
        QueryOption queryOption = new QueryOption(page, limit, sortBy, sortDirection, searchQuery, "", true);
        return ApiResponse.success(permissionService.getPermissions(queryOption));
    }

    @GetMapping("/roles/{roleId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ApiResponse<List<PermissionResponse>> getPermissionsByRole(@PathVariable Long roleId) {
        return ApiResponse.success(permissionService.getPermissionsByRole(roleId));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ApiResponse<PermissionResponse> getPermissionById(@PathVariable Long id) {
        return ApiResponse.success(permissionService.getPermissionById(id));
    }

    @PostMapping("/assign")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ApiResponse<PermissionResponse> assignPermissionToRole(@RequestBody @Valid AssignPermissionToRoleRequest request) {
        return ApiResponse.success(permissionService.assignPermissionToRole(request));
    }


    // didn't test this endpoint and below
    @PutMapping("/roles/{roleId}/permissions")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ApiResponse<List<PermissionResponse>> updatePermissionsForRole(@PathVariable Long roleId, @RequestBody @Valid UpdateAssignPermissionToRoleRequest request) {
        return ApiResponse.success(permissionService.updatePermissionsForRole(roleId, request.permissionIds()));
    }

//    @DeleteMapping("/{id}")
//    @PreAuthorize("hasRole('ADMIN')")
//    public ApiResponse<Void> deletePermission(@PathVariable Long id) {
//        permissionService.deletePermission(id);
//        return ApiResponse.success();
//    }


}
