package net.nemisolv.techshop.service;

import net.nemisolv.techshop.payload.PagedResponse;
import net.nemisolv.techshop.payload.QueryOption;
import net.nemisolv.techshop.payload.permission.AssignPermissionToRoleRequest;
import net.nemisolv.techshop.payload.permission.PermissionRequest;
import net.nemisolv.techshop.payload.permission.PermissionResponse;

import java.util.List;

public interface PermissionService {
//    PermissionResponse createPermission(PermissionRequest request);
//    PermissionResponse updatePermission(Long id, PermissionRequest request);
    void deletePermission(Long id);
    PagedResponse<PermissionResponse> getPermissions(QueryOption queryOption);
    PermissionResponse getPermissionById(Long id);
    List<PermissionResponse> getPermissionsByRole(Long roleId);

    PermissionResponse assignPermissionToRole(AssignPermissionToRoleRequest request);

    List<PermissionResponse> updatePermissionsForRole(Long roleId, List<Long> permissionIds);
}
